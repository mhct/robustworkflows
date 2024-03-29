package be.kuleuven.robustworkflows.model.antactors.dmas;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.util.Utils;
/**
 * 
 * The TalkerAntActor asks for QoS values for a given {@link ServiceType} to a group of agents, given 
 * by the message {@link Neighbors}.
 * 
 * It accumulates the replies and returns all replies to its parent.
 * 
 * @author mario
 *
 * === Inbound messages ===
 * - ''' ExplorationReply '''
 * - ''' AskQoSTimeout '''
 * - ''' Neighbors ''' from DmasMW
 * 
 * === Outbound messages ===
 * - ''' ExplorationRequest '''
 * - ''' DMASImmutableExplorationRepliesHolder '''
 */
public class DMASTalkerAntActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final String antName;
	private final ServiceType serviceType;
	private final long explorationTimeout;
	
	private Neighbors neighbors;
	private int receivedReplies = 0;
	private int askedQos;
	private boolean ignoreTimeout = false;
	private DMASExplorationRepliesHolder repliesHolder;
	private int nbMessages;

	public DMASTalkerAntActor(ServiceType serviceType, long explorationTimeout) {
		log.debug("DMASTalker created" + this.toString());
		this.serviceType = serviceType;
		this.explorationTimeout = explorationTimeout;
		this.antName = self().path().toString();
				
		repliesHolder = DMASExplorationRepliesHolder.getInstance();
	}

	@Override
	public void onReceive(final Object message) throws Exception {
		incrementNbMessages();
		if (Neighbors.class.isInstance(message)){
			log.debug("Adding AskQoSTimeout: " + explorationTimeout);
			Utils.addExpirationTimer(context(), explorationTimeout, EventType.AskQoSTimeout);
			neighbors = (Neighbors) message;
			askQoS(neighbors.getNeighbors());
			ignoreTimeout = false;
			
		} else if (ExplorationReply.class.isInstance(message)) {
			log.debug("Got ExplorationReply: " + message.toString());
			log.debug("context().parent(): " + context().parent().path());
			
			receivedReplies++;
			ExplorationReply explorationReply = (ExplorationReply) message;

			//
			// check if it is a proper reply
			//
			if (explorationReply.isPossible()) {
				repliesHolder.add(explorationReply, sender());
			}
			
			if (receivedReplies == askedQos) {
				log.debug("RECEIVED == ASKEDQOS ... ->context().parent(): " + context().parent().path());
				persistMessagingInfo();
				context().parent().tell(repliesHolder.getImmutableClone(), self());
				repliesHolder.clear();
				resetNbMessages();
				ignoreTimeout  = true;
			}
			
		} else if (!ignoreTimeout && EventType.AskQoSTimeout.equals(message)) {
			persistMessagingInfo();
			log.debug("AskQoSTimeout, replying to parent");
			context().parent().tell(repliesHolder.getImmutableClone(), self());
			repliesHolder.clear();
			resetNbMessages();
		} else {
			log.debug("Unhandled message: " + message);
			unhandled(message);
		}
	}
	
	private void persistMessagingInfo() {
		final long currentTime = System.currentTimeMillis();
			log.info("message=" + currentTime + "," + antName + ","  + getNbMessages());
			resetNbMessages();
	}
	
	private void resetNbMessages() {
		nbMessages = 0;
	}
	
	private int getNbMessages() {
		return nbMessages;
	}
	
	private void incrementNbMessages() {
		nbMessages++;
	}

	/**
	 * Sends a ExplorationRequest message to the agents given at the list
	 * agentPaths
	 * 
	 * @param agentPaths Paths to agents to be contacted
	 * @param serviceType Desired ServiceType to be explored
	 */
	private void askQoS(List<ActorRef> agents) {
		repliesHolder.clear();
		askedQos = 0;
		receivedReplies = 0;
		for (ActorRef agent: agents) {
			if (agent != null) {
				log.debug("Sending ExplorationRequest to: " + agent.path());
				askedQos++;
				agent.tell(ExplorationRequest.getInstance(1, serviceType, 10, self(), context().parent().path().name()), self());
			}
		}
		log.debug("AskQos to: " + askedQos);
	}
	
	/**
	 * Props Factory for a TalkerAntActor
	 * 
	 * @param serviceType
	 * @param explorationTimeout in milliseconds
	 * @param samplingProbability probability to explore neighbors [0.0, 1.0]
	 * @return a Props capable of creating a TalkerAntActor
	 */
	public static Props props(final ServiceType serviceType, final long explorationTimeout) {
		return Props.create(new Creator<DMASTalkerAntActor>() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public DMASTalkerAntActor create() throws Exception {
				return new DMASTalkerAntActor(serviceType, explorationTimeout);
			}
		});	
	}

}
