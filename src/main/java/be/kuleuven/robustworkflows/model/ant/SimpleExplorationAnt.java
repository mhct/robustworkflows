package be.kuleuven.robustworkflows.model.ant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.Workflow;

/**
 * Explores the network looking for only one service
 * 
 * @author marioct
 *
 * === Inbound messages ===
 * - ''' ExploreService ''' 
 * - ''' ExplorationReply '''
 * - ''' ExploringStateTimeout '''
 * - ''' ExplorationFinished '''
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * - ''' ExplorationRequest '''
 * 
 */
public class SimpleExplorationAnt extends UntypedActor {

	private final ActorRef master;
	private final ModelStorage modelStorage;
	private int waitForReply = 0;
	private final RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
	private List<ExplorationReplyWrapper> replies = new ArrayList<ExplorationReplyWrapper>();
	
	public SimpleExplorationAnt(ActorRef master, ModelStorage modelStorage, long explorationTimeout) {
		this.master = master;
		this.modelStorage = modelStorage;
		addExpirationTimer(explorationTimeout, EventType.ExploringStateTimeout);
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (ExploreService.class.isInstance(message)) {
			ExploreService msg = (ExploreService) message;
			
			askQoS(modelStorage.getFactoryAgents(msg.getServiceType()), msg.getServiceType());
			
		} else if (ExplorationReply.class.isInstance(message)) {
			ExplorationReply qos = (ExplorationReply) message;
			modelStorage.persistEvent(qos.toDBObject(sender().path().name()));

			replies.add(new ExplorationReplyWrapper(sender(), qos));
			
			waitForReply--;
			if (waitForReply == 0) {
				addExpirationTimer(0, EventType.ExplorationFinished);
			}
			
			
		} else if (EventType.ExploringStateTimeout.equals(message) || EventType.ExplorationFinished.equals(message)) {
			modelStorage.persistEvent("ExpAnt Timeout"); //add complete summary of the state of explorationant
			master.tell(bestReply(), self());
		}
	}
	
	private SimpleExplorationResult bestReply() {
		Collections.sort(replies, new Comparator<ExplorationReplyWrapper>() {

			@Override
			public int compare(ExplorationReplyWrapper o1, ExplorationReplyWrapper o2) {
				final long o1Time = o1.getReply().getComputationTime();
				final long o2Time = o2.getReply().getComputationTime();
				
				if (o1Time < o2Time) {
					return -1;
				} else if (o2Time > o2Time) {
					return 1;
				}
					
				return 0;
			}
			
		});
		
		if (replies.size() >= 1) {
			//TODO move to EngageState
			return SimpleExplorationResult.getInstance(replies.get(0).getActor(), replies.get(0).getReply().getComputationTime());
		}
		
		return null;
	}
	
	/**
	 * Sends a ServiceRequestExploration message to the agents given at the list
	 * agentPaths
	 * 
	 * @param agentPaths Paths to agents to be contacted
	 * @param serviceType Desired ServiceType to be explored
	 */
	private void askQoS(List<String> agentPaths, ServiceType serviceType) {
		for (String agentPath: agentPaths) {
			if (sampling()) {
				ActorRef agent = context().actorFor(agentPath);
				if (agent != null) {
					agent.tell(ExplorationRequest.getInstance(0, serviceType, 10, self(), master.path().name()), self());
					waitForReply ++;
				}
			}
		}
		
	}
	
	/**
	 * Defines if a value should be used or not. returns true with a probability of 33%.
	 * Uniform sampling
	 * @return
	 */
	private boolean sampling() {
		if (random.nextUniform(0, 1) >= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	//FIXME duplicated code with CompositeExplorationAnt
	public void addExpirationTimer(long time, final EventType message) {
		context().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, self());
					}
		}, context().system().dispatcher());
	}

	public static UntypedActor getInstance(ActorRef master, ModelStorage modelStorage, Workflow workflow, long explorationTimeout) {
		return new SimpleExplorationAnt(master, modelStorage, explorationTimeout);
	}
}

class ExplorationReplyWrapper {
	private ExplorationReply reply;
	private ActorRef actor;

	public ExplorationReply getReply() {
		return reply;
	}
	
	public ActorRef getActor() {
		return actor;
	}
	public ExplorationReplyWrapper (ActorRef actor, ExplorationReply reply) {
		this.actor = actor;
		this.reply = reply;
	}
}
