package be.kuleuven.robustworkflows.model.antactors.dmas2;

import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ServiceType;
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
 * - ''' Compose ''' tell the explorationAnt to mark all its events as belonging to another RUN of the emulation
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * 
 */
public class DMAS2ExplorationAntActor extends UntypedActor implements DMAS2ExplorationAntContext {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private Set<ActorRef> pathFollowed;
	private long explorationTimeout;
	
	private ActorRef currentAgent; //Current Node that the exploration ant is "seating"
	
	private double samplingProbability;

	private StateMachine<Object, DMAS2ExplorationAntContext> fsm;
	
	public DMAS2ExplorationAntActor(ActorRef master, long explorationTimeout, double samplingProbability) {
		log.info("DMAS2ExpAntActor created" + this.getSelf().path().toString());
		
		this.currentAgent = master;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
		this.pathFollowed = Sets.newHashSet();
		
		DMAS2IdleState idle = new DMAS2IdleState();
		DMAS2ExploringState exploring = new DMAS2ExploringState();
		
		StateMachineBuilder<Object, DMAS2ExplorationAntContext> builder = StateMachine.create(idle);
		builder.addTransition(idle, StartExperimentRun.class, idle);
		builder.addTransition(idle, ExploreService.class, exploring);
		builder.addTransition(idle, EventType.ExploringStateTimeout.getClass(), idle);
		builder.addTransition(exploring, String.class, idle);
		builder.addTransition(exploring, DMAS2ImmutableExplorationRepliesHolder.class, exploring);
		builder.addTransition(exploring, Neighbors.class, exploring);
		builder.addTransition(exploring, EventType.ExploringStateTimeout.getClass(), exploring);
		
		fsm = builder.build();
	}

	@Override
	public void postStop() {
	}
	
	@Override
	public void onReceive(Object message) {
		try {
			fsm.handle(message, this);
		} catch (RuntimeException e) {
			log.debug("Wrong state." + e.toString());
		}
	}
	
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
	public void addAgentToVisitedNodes(ActorRef currentAgent) {
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
	
	@Override
	public LoggingAdapter getLoggingAdapter() {
		return log;
	}

	public static UntypedActor getInstance(ExplorationAntParameter parameterObject) {
		return new DMAS2ExplorationAntActor(parameterObject.getMaster(),
				parameterObject.getExplorationTimeout(),
				parameterObject.getSamplingProbability());
	}

}
