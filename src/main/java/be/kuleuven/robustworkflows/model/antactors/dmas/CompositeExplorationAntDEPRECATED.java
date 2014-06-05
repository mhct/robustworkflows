package be.kuleuven.robustworkflows.model.antactors.dmas;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.WorkflowServiceMatcher;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Workflow;

/**
 * Explores the Agent graph looking for good services (QoS)
 * Myopic Ant, minimizes the time for EACH service and does not look at reservations, or probabilities, for instance.
 *  
 * @author mario
 *
 * * === Inbound messages ===
 * - ''' ExplorationReply '''
 * - ''' ExploringStateTimeout '''
 * - ''' ExplorationFinished '''
 * - ''' Compose ''' tell the explorationAnt to mark all its events as bellonging to another RUN of the emulation
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * - ''' ExplorationRequest '''
 * 
 */
public class CompositeExplorationAntDEPRECATED extends UntypedActor {
	
	private WorkflowServiceMatcher serviceMatcher;
	private final Workflow workflow;
	private final ActorRef master;
	private int explorationCounter = 0;
	private int waitForReply = 0;
	private String cachedName;
	private final long explorationTimeout;
	private final RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());

	public CompositeExplorationAntDEPRECATED(ActorRef master, Workflow workflow, long explorationTimeout) {
		this.master = master;
		this.workflow = workflow;
		this.explorationTimeout = explorationTimeout;
	}

	private String masterName() {
		if (cachedName == null) {
			cachedName = master.path().name();
		}
		
		return cachedName;
	}
	
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (EventType.RUN.equals(message)) {
//			modelStorage.persistEvent(selfName() + " received " + message);
			
			serviceMatcher = WorkflowServiceMatcher.getInstance(workflow);
			for (ServiceType st: serviceMatcher.getNeededServiceTypes()) {
//				List<String> agentPaths = modelStorage.getFactoryAgents(st);
//				askQoS(agentPaths, st);
			}
			addExpirationTimer(explorationTimeout, EventType.ExploringStateTimeout);
			
		} else if (ExplorationReply.class.isInstance(message)) {
			ExplorationReply qos = (ExplorationReply) message;
//			modelStorage.persistEvent(qos.toDBObject(sender().path().name()));

			serviceMatcher.addReply(sender(), qos);
			waitForReply--;
			if (waitForReply == 0) {
				addExpirationTimer(0, EventType.ExplorationFinished);
			}
			
			
		} else if (EventType.ExploringStateTimeout.equals(message) || EventType.ExplorationFinished.equals(message)) {
//			modelStorage.persistEvent("ExpAnt Timeout"); //add complete summary of the state of explorationant
			master.tell(ExplorationResult.getInstance(serviceMatcher.createOptimalWorkflow()), self());
		}
		
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
					agent.tell(ExplorationRequest.getInstance(explorationCounter++, serviceType, 10, self(), masterName()), self());
					waitForReply++;
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

	public void addExpirationTimer(long milliseconds, final EventType message) {
		context().system().scheduler().scheduleOnce(Duration.create(milliseconds, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, self());
					}
		}, context().system().dispatcher());
	}
	
	public static UntypedActor getInstance(
			ExplorationAntParameter antParameters) {
		
		return new CompositeExplorationAntDEPRECATED(
				antParameters.getMaster(),
				antParameters.getWorkflow(),
				antParameters.getExplorationTimeout());
	}

}
