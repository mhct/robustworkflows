package be.kuleuven.robustworkflows.model.antactors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.util.Utils;

import com.google.common.collect.Lists;
/**
 * 
 * The TalkerAntActor asks for QoS values for a given {@link ServiceType} to a group of agents, given 
 * by the message {@link Neighbors}.
 * 
 * It accumulates the replies and selects the best one, returning the best reply to its parent.
 * 
 * @author marioct
 *
 * === Inbound messages ===
 * - ''' ExplorationReply '''
 * - ''' AskQoSTimeout '''
 * - ''' Neighbors ''' from DmasMW
 * 
 * === Outbound messages ===
 * - ''' ExplorationRequest '''
 * - ''' ExplorationReplyWrapper '''
 */
public class TalkerAntActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private RandomDataGenerator randomSampling = new RandomDataGenerator(new MersenneTwister());
	
	private final ServiceType serviceType;
	private final long explorationTimeout;
	private final double samplingProbability;
	
	private Neighbors neighbors;

	private List<ExplorationReplyWrapper> replies;
	private int receivedReplies = 0;

	private int askedQos;

	private boolean ignoreTimeout = false;


	public TalkerAntActor(ServiceType serviceType, long explorationTimeout, double samplingProbability) {
		this.serviceType = serviceType;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
		
		replies = Lists.newArrayList();
	}

	@Override
	public void onReceive(final Object message) throws Exception {
//		if (ActorRef.class.isInstance(message) ) {
//			//explore neighbors
//			final ActorRef current = (ActorRef) message;
//			log.debug("Asking NeihgborListRequest to: " + current.path());
//			current.tell(EventType.NeihgborListRequest, self());

//		} else if (Neighbors.class.isInstance(message)){
		if (Neighbors.class.isInstance(message)){
//			log.debug("Received Neighbors list");
			neighbors = (Neighbors) message;
			askQoS(neighbors.getNeighbors());
			
		} else if (ExplorationReply.class.isInstance(message)) {
			log.debug("Got ExplorationReply: " + message.toString());
			receivedReplies++;
			ExplorationReply explorationReply = (ExplorationReply) message;
			//
			// test for the reply, to check if it is a proper reply
			//
			if (explorationReply.isPossible()) {
				replies.add(ExplorationReplyWrapper.getInstance(sender(), explorationReply));
			}
			
			if (receivedReplies == askedQos) {
				context().parent().tell(bestExplorationReply(), self());
				ignoreTimeout  = true;
			}
			
		} else if (!ignoreTimeout && EventType.AskQoSTimeout.equals(message)) {
			context().parent().tell(bestExplorationReply(), self());
		}
	}
	
	/**
	 * Sends a ServiceRequestExploration message to the agents given at the list
	 * agentPaths
	 * 
	 * @param agentPaths Paths to agents to be contacted
	 * @param serviceType Desired ServiceType to be explored
	 */
	private void askQoS(List<ActorRef> agents) {
		Utils.addExpirationTimer(context(), explorationTimeout, EventType.AskQoSTimeout);

		askedQos = 0;
		for (ActorRef agent: agents) {
			if (sampling()) {
				if (agent != null) {
					log.debug("Sending ExplorationRequest to: " + agent.path());
					askedQos++;
					agent.tell(ExplorationRequest.getInstance(1, serviceType, 10, self(), context().parent().path().name()), self());
				}
			}
		}
		log.debug("AskQos to: " + askedQos);
	}
	
	/**
	 * Defines if a value should be used or not. returns true with a probability of samplingProbability.
	 * Uniform sampling
	 * @return
	 */
	private boolean sampling() {
		if (randomSampling.nextUniform(0, 1) <= samplingProbability) {
			return true;
		} else {
			return false;
		}
	}
	
	private ExplorationReplyWrapper bestExplorationReply() {
		if (replies.size() == 0) {
			log.debug("nbReceivedReplies: " + receivedReplies + ", nbReplies: " + replies.size());
			return ExplorationReplyWrapper.getInstance(self(), null);
		} else if (replies.size() == 1) {
			return replies.get(0);
		} else {
			Collections.sort(replies, new Comparator<ExplorationReplyWrapper>() {
	
				@Override
				public int compare(ExplorationReplyWrapper obj1, ExplorationReplyWrapper obj2) {
					final long o1 = obj1.getReply().getComputationTime();
					final long o2 = obj2.getReply().getComputationTime();
					
					if (o1 < o2) {
						return -1;
					} else if (o1 > o2) {
						return 1;
					}
						
					return 0;
				}
				
			});
		
			return replies.get(0);
		}
	}

	
	/**
	 * Props Factory for a TalkerAntActor
	 * 
	 * @param serviceType
	 * @param explorationTimeout in milliseconds
	 * @param samplingProbability probability to explore neighbors [0.0, 1.0]
	 * @return a Props capable of creating a TalkerAntActor
	 */
	public static Props props(final ServiceType serviceType, final long explorationTimeout, final double samplingProbability) {
		return new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() throws Exception {
				return new TalkerAntActor(serviceType, explorationTimeout, samplingProbability);
			}
		});	
	}

}
