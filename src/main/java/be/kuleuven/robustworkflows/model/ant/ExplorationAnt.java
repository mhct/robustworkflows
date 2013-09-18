package be.kuleuven.robustworkflows.model.ant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import scala.concurrent.duration.Duration;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
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
 */
public class ExplorationAnt extends UntypedActor {
	
	private final ModelStorage modelStorage;
	private WorkflowServiceMatcher serviceMatcher;
	private final Workflow workflow;
	private final ActorRef master;
	private int explorationCounter = 0;
	private int waitForReply = 0;
	private String cachedName;
	private final long explorationTimeout;
	private final RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());

	public ExplorationAnt(ActorRef master, ModelStorage modelStorage, Workflow workflow, long explorationTimeout) {
		this.master = master;
		this.modelStorage = modelStorage;
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
				List<String> agentPaths = modelStorage.getFactoryAgents(st); //FIXME CoordinationLayer function
				askQoS(agentPaths, st);
			}
			addExpirationTimer(explorationTimeout, EventType.ExploringStateTimeout);
			
		} else if (ExplorationReply.class.isInstance(message)) {
			ExplorationReply qos = (ExplorationReply) message;
			modelStorage.persistEvent(qos.toDBObject(sender().path().name()));

			serviceMatcher.addReply(sender(), qos);
			waitForReply--;
			if (waitForReply == 0) {
				addExpirationTimer(0, EventType.ExplorationFinished);
			}
			
			
		} else if (EventType.ExploringStateTimeout.equals(message) || EventType.ExplorationFinished.equals(message)) {
			modelStorage.persistEvent("ExpAnt Timeout"); //add complete summary of the state of explorationant
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

	public void addExpirationTimer(long time, final EventType message) {
//		system.scheduler().scheduleOnce(Duration.create(10, TimeUnit.SECONDS), getClientAgent(), system.dispatcher(), null);
		context().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, self());
					}
		}, context().system().dispatcher());
	}
	
	public static Actor getInstance(ActorRef master, ModelStorage modelStorage, Workflow workflow, long explorationTimeout) {
		return new ExplorationAnt(master, modelStorage, workflow, explorationTimeout);
	}

}
