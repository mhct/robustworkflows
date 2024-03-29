package be.kuleuven.robustworkflows.model.antactors.dmas;

import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
import be.kuleuven.robustworkflows.model.messages.Workflow;
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
 * - ''' Workflow ''' request to find component services to work in the workflow 
 * - ''' ExploringStateTimeout '''
 * - ''' ExplorationFinished '''
 * - ''' Compose ''' tell the explorationAnt to mark all its events as belonging to another RUN of the emulation
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * 
 */
public class DMASExplorationAntActor extends UntypedActor implements DMASExplorationAntContext {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private Set<ActorRef> pathFollowed;
	private long explorationTimeout;
	private double samplingProbability;
	private StateMachine<Object, DMASExplorationAntContext> fsm;
	
	public DMASExplorationAntActor(ActorRef master, long explorationTimeout, double samplingProbability) {
		log.debug("DMASExpAntActor created" + this.toString());
		
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
		this.pathFollowed = Sets.newHashSet();
		
		createFSMBasedOnWorkflow();
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
	
	private void createFSMBasedOnWorkflow() {
		DMASExplorationAntIdleState idle = new DMASExplorationAntIdleState();
		DMASExplorationAntExploringState exploring = new DMASExplorationAntExploringState();
		StateMachineBuilder<Object, DMASExplorationAntContext> builder = StateMachine.create(idle);
		builder.addTransition(idle, StartExperimentRun.class, idle);
		builder.addTransition(idle, Workflow.class, exploring);
		builder.addTransition(idle, EventType.ExploringStateTimeout.getClass(), idle);
		builder.addTransition(exploring, String.class, idle);
		builder.addTransition(exploring, DMASImmutableExplorationRepliesHolder.class, exploring);
		builder.addTransition(exploring, Neighbors.class, exploring);
		builder.addTransition(exploring, EventType.ExploringStateTimeout.getClass(), exploring);
		
		fsm = builder.build();
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
	public void addAgentToVisitedNodes(ActorRef currentAgent) {
		pathFollowed.add(currentAgent);
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
		return new DMASExplorationAntActor(parameterObject.getMaster(),
				parameterObject.getExplorationTimeout(),
				parameterObject.getSamplingProbability());
	}

}
