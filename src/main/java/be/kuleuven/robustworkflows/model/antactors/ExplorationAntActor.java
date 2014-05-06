package be.kuleuven.robustworkflows.model.antactors;

import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.ant.messages.ExploreService;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
import be.kuleuven.robustworkflows.util.StateMachine;
import be.kuleuven.robustworkflows.util.StateMachine.StateMachineBuilder;

import com.google.common.collect.Sets;

/**
 * Explores the network looking for only one service.
 * This is a completely reactive ExplorationAnt. It never "remembers" places that it have been, from previous
 * service searches. It can avoid loops, though.
 * 
 *  Current ExploringState strategy is to keep looking for agents of the proper {@link ServiceType} only until
 *  finding at least ONE agent which offers services of the desired type. Notice that there is no loadbalancing in such
 *  solution.
 * 
 * @author marioct
 *
 * === Inbound messages ===
 * - ''' ExploreService ''' 
 * - ''' ExploringStateTimeout '''
 * - ''' ExplorationFinished '''
 * - ''' Compose ''' tell the explorationAnt to mark all its events as bellonging to another RUN of the emulation
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * 
 */
public class ExplorationAntActor extends UntypedActor implements ExplorationAntContext {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private Set<ActorRef> pathFollowed;
	private long explorationTimeout;
	
	private ActorRef currentAgent; //Current Node that the exploration ant is "seating"

	private double samplingProbability;

	private StateMachine<Object, ExplorationAntContext> fsm;
	
	public ExplorationAntActor(ActorRef master, long explorationTimeout, double samplingProbability) {
//		this.master = master;
		this.currentAgent = master;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
		this.pathFollowed = Sets.newHashSet();
		
		IdleState idle = new IdleState();
		ExploringState exploring = new ExploringState();
		StateMachineBuilder<Object, ExplorationAntContext> builder = StateMachine.create(idle);
		builder.addTransition(idle, StartExperimentRun.class, idle);
		builder.addTransition(idle, ExploreService.class, exploring);
		builder.addTransition(idle, EventType.ExploringStateTimeout.getClass(), idle);
		builder.addTransition(exploring, String.class, idle);
		builder.addTransition(exploring, ExplorationReplyWrapper.class, exploring);
		builder.addTransition(exploring, Neighbors.class, exploring);
		builder.addTransition(exploring, EventType.ExploringStateTimeout.getClass(), exploring);
		
		fsm = builder.build();
	}

	@Override
	public void postStop() {
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		fsm.handle(message, this);
	}
	
//	private void shouldExploreAgain() {
//		if (receivedReplies == askedQos && replies.size() < minimum_nbRplies) {
//			exploreAgain();
//		}
//	}
	
//	private void exploreAgain() {
//		// selects a new agent from the neighbors it knows
//		receivedReplies = 0;
//		askedQos = 0;
//		boolean continueSearch = true;
//		
//		if (neighbors.isEmpty()) {
//			log.info("nbNeighbors = 0");
//			return;
//		}
//		
//		ActorRef tentativeAgent = null;
//		while (continueSearch) {
//			tentativeAgent = neighbors.getRandomNeighbor();
//			if (pathFollowed.contains(tentativeAgent)) {
//				continueSearch = true;
//			} else {
//				break;
//			}
//		}
//		if (tentativeAgent == null) {
//			log.error("nbNeighbors = 0");
//		}
//		
//		currentAgent = tentativeAgent;
//		pathFollowed.add(currentAgent);
//		currentAgent.tell(EventType.NeihgborListRequest, self());
//	}
	
	@Override
	public long getExplorationTimeout() {
		return explorationTimeout;
	}

	@Override
	public double getSamplingProbability() {
		return samplingProbability;
	}

	@Override
	public ActorRef getCurrentAgent() {
		return currentAgent;
	}

	@Override
	public void addToVisitedNodes(ActorRef currentAgent) {
		pathFollowed.add(currentAgent);
	}

	@Override
	public void setCurrentAgent(ActorRef actor) {
		currentAgent = actor;
	}

	@Override
	public void tellMaster(Object message) {
		getContext().parent().tell(message, self());
	}
	
	public static UntypedActor getInstance(ExplorationAntParameter parameterObject) {
		return new ExplorationAntActor(parameterObject.getMaster(),
				parameterObject.getExplorationTimeout(),
				parameterObject.getSamplingProbability());
	}
}
