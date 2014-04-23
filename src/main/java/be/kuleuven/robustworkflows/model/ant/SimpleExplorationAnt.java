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
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.EmptyExplorationResult;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.events.ExplorationAntEvents;
import be.kuleuven.robustworkflows.model.events.ExplorationReplyEvent;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
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
 * - ''' Compose ''' tell the explorationAnt to mark all its events as bellonging to another RUN of the emulation
 * - ''' Neighbors ''' from DmasMW
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * - ''' ExplorationRequest '''
 * - ''' NeihgborListRequest ''' from DmasMW
 * 
 */
public class SimpleExplorationAnt extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final ActorRef master;
	private final ModelStorage modelStorage;
	private RandomDataGenerator randomSampling = new RandomDataGenerator(new MersenneTwister());
	private Integer linearID = 0;
	private List<ExplorationReplyWrapper> replies = new ArrayList<ExplorationReplyWrapper>();
	private long explorationTimeout;
	private double samplingProbability;

	private ServiceType serviceType;

	public SimpleExplorationAnt(ActorRef master, ModelStorage modelStorage, long explorationTimeout, double samplingProbability) {
		this.master = master;
		this.modelStorage = modelStorage;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
	}

	@Override
	public void postStop() {
		modelStorage.persistWriteCache();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (StartExperimentRun.class.isInstance(message)) {
			StartExperimentRun msg = (StartExperimentRun) message;
			modelStorage.addField("run", msg.getRun());
			modelStorage.persistWriteCache();
			randomSampling = new RandomDataGenerator(new MersenneTwister());
			replies = new ArrayList<ExplorationReplyWrapper>();
			
		} else if (ExploreService.class.isInstance(message)) {
			addExpirationTimer(explorationTimeout, EventType.ExploringStateTimeout);
			ExploreService msg = (ExploreService) message;
			
			// a new step. The EA has to find factories which provide the desired service. msg.getServiceType()
			// 1 - retrieve list of neighbors from master
			// 2 - ask QOS from neighbors
			
//			askQoS(modelStorage.getShuffledFactoryAgents(msg.getServiceType()), msg.getServiceType());
			serviceType = msg.getServiceType();
			log.debug("Asking NeihgborListRequest to: " + master.path());
			master.tell(EventType.NeihgborListRequest, self());
			
		} else if (Neighbors.class.isInstance(message)){
			log.debug("Received Neighbors list");
			Neighbors neighbors = (Neighbors) message;
		
			askQoS(neighbors.getNeighbors(), serviceType);
			
		} else if (ExplorationReply.class.isInstance(message)) {
			log.debug("Got ExplorationReply");
			ExplorationReply explorationReply = (ExplorationReply) message;
			modelStorage.persistEvent(ExplorationReplyEvent.instance(explorationReply, sender().path().name()));

			replies.add(new ExplorationReplyWrapper(sender(), explorationReply));
			
		} else if (EventType.ExploringStateTimeout.equals(message) || EventType.ExplorationFinished.equals(message)) {
			modelStorage.persistEvent(ExplorationAntEvents.timeout(master.path().name()));
			log.debug("nbReplies: " + replies.size());
			
			SimpleExplorationResult bla = bestReply();
			replies.clear();
			
			if (bla == null) {
				log.debug("ExplorationAnt: Didn't receive any replies");
				master.tell(new EmptyExplorationResult(), self());
			} else {
				master.tell(bla, self());
			}
		}
	}
	
	private SimpleExplorationResult bestReply() {
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
		
		if (replies.size() >= 1) {
			return SimpleExplorationResult.getInstance(replies.get(0).getActor(), replies.get(0).getReply().getComputationTime(), replies.get(0).getReply().getRequestExploration().getServiceType());
		} else {
			modelStorage.persistEvent(ExplorationAntEvents.noReplies(master.path().name()));
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
	private void askQoS(List<ActorRef> agents, ServiceType serviceType) {
		for (ActorRef agent: agents) {
			if (sampling()) {
				if (agent != null) {
					log.debug("Sending ExplorationRequest to: " + agent.path());
					agent.tell(ExplorationRequest.getInstance(linearID++, serviceType, 10, self(), master.path().name()), self());
				}
			}
		}
		
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

	public static UntypedActor getInstance(ActorRef master, Workflow workflow, ExplorationAntParameter parameterObject) {
		return null;
	}	
	
	public static UntypedActor getInstance(ExplorationAntParameter parameterObject) {
		return new SimpleExplorationAnt(parameterObject.getMaster(),
				parameterObject.getModelStorage(),
				parameterObject.getExplorationTimeout(),
				parameterObject.getSamplingProbability());
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

	@Override
	public String toString() {
		return "ExplorationReplyWrapper [reply=" + reply + ", actor=" + actor
				+ "]";
	}
	
	
}
