package be.kuleuven.robustworkflows.model.ant;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.Workflow;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.QoSData;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;

import com.google.common.collect.Maps;

public class ExplorationAnt extends UntypedActor {
	
	private final long EXPLORATION_TIMEOUT = 1000;
	private final ModelStorage modelStorage;
	private final Workflow workflow;
	private final ActorRef master;
	private final Map<ActorRef, QoSData> replies = Maps.newHashMap();

	public ExplorationAnt(ActorRef master, ModelStorage modelStorage, Workflow workflow) {
		this.master = master;
		this.modelStorage = modelStorage;
		this.workflow = workflow;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (EventType.RUN.equals(message)) {
			modelStorage.persistEvent(self() + " received " + message);
			
			master.tell(EventType.NeihgborListRequest, self());
			addExpirationTimer(EXPLORATION_TIMEOUT, EventType.ExploringStateTimeout);
			
		} else if (Neighbors.class.isInstance(message)) {
			modelStorage.persistEvent(self() + " received " + message);
			for (ActorRef agent: ((Neighbors)message).getNeighbors()) {
				agent.tell(ServiceRequestExploration.getInstance(workflow.get(0), 10, self()), self());
			}
			
		} else if (QoSData.class.isInstance(message)) {
			modelStorage.persistEvent(self() + " received " + message);
			QoSData qos = (QoSData) message;
			replies.put(sender(), qos);
			
		} else if (EventType.ExploringStateTimeout.equals(message)) {
			modelStorage.persistEvent("ExpAnt Timeout");
			master.tell(ExplorationResult.getInstance(replies), self());
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
