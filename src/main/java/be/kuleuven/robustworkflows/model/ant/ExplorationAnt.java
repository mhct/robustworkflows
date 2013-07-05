package be.kuleuven.robustworkflows.model.ant;

import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExplorationReply;
import be.kuleuven.robustworkflows.model.messages.Workflow;

/**
 * Explores the Agent graph looking for good services (QoS)
 * 
 * @author mario
 *
 */
public class ExplorationAnt extends UntypedActor {
	
	private final long EXPLORATION_TIMEOUT = 1000;
	private final ModelStorage modelStorage;
	private final WorkflowServiceMatcher workflow;
	private final ActorRef master;
	private int explorationCounter = 0;
//	private final Map<ActorRef, QoSData> replies = Maps.newHashMap();

	public ExplorationAnt(ActorRef master, ModelStorage modelStorage, Workflow workflow) {
		this.master = master;
		this.modelStorage = modelStorage;
		this.workflow = WorkflowServiceMatcher.getInstance(workflow);
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (EventType.RUN.equals(message)) {
			modelStorage.persistEvent(self() + " received " + message);
			
			for (ServiceType st: workflow.getNeededServiceTypes()) {
				List<String> agentPaths = modelStorage.getFactoryAgents(st);
				askQoS(agentPaths, st);
			}
			addExpirationTimer(EXPLORATION_TIMEOUT, EventType.ExploringStateTimeout);
			
		} else if (ServiceRequestExplorationReply.class.isInstance(message)) {
			//add information to the required type of service FIXME fix the event type
			modelStorage.persistEvent(self() + " received " + message);
			ServiceRequestExplorationReply qos = (ServiceRequestExplorationReply) message;
			workflow.associateAgentToTask(sender(), qos);
			
			if (workflow.getNeededServiceTypes().size() == 0) {
				master.tell(ExplorationResult.getInstance(workflow), self());
			}
//			replies.put(sender(), qos);
			
			
		} else if (EventType.ExploringStateTimeout.equals(message) || EventType.ExplorationFinished.equals(message)) {
			modelStorage.persistEvent("ExpAnt Timeout");
//			master.tell(ExplorationResult.getInstance(replies), self());
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
			ActorRef agent = context().actorFor(agentPath);
			if (agent != null) {
				agent.tell(ServiceRequestExploration.getInstance(explorationCounter++, serviceType, 10, self()), self());
			}
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

	public static Actor getInstance(ActorRef master, ModelStorage modelStorage, Workflow workflow) {
		return new ExplorationAnt(master, modelStorage, workflow);
	}

}
