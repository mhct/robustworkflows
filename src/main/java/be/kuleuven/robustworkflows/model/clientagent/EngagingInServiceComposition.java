package be.kuleuven.robustworkflows.model.clientagent;

import java.util.Map;

import com.mongodb.BasicDBObject;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;
import akka.actor.ActorRef;

/**
 * FIXME when an agent is engaging in a composition, it has to maintain information and engage with differet providers... 
 * better to create a more evolved set of abstractions to deal with that
 * 
 * FIXME currently it is only engaging with ServiceType A
 * 
 * @author mario
 *
 */
public class EngagingInServiceComposition extends ClientAgentState {

	private ExplorationResult selectedComposition;

	private EngagingInServiceComposition(ClientAgentProxy clientAgentProxy, ExplorationResult selected) {
		super(clientAgentProxy);
		this.selectedComposition = selected;
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			//FIXME this is a hack. create new abstraction to perform this operation. (IntentionAnts)
			for (Map.Entry<ActorRef, ServiceType> e: selectedComposition.getAgentServicesMap().entrySet()) {
				persistEvent("Engaging in Service Composition: " + e.getKey().path().name()); //FIXME add ClientAgent name
				e.getKey().tell(ServiceRequest.getInstance(e.getValue()), getClientAgentProxy().self()); //FIXME service type is fixed...
			}
			
		} else if (ServiceRequestFinished.class.isInstance(message)){
			persistEvent("ServiceComposition FINISHED");
			persistEvent(summaryRequest((ServiceRequestFinished) message));
			
			
			//TODO add more information about this particular event.
			//TODO, look a better way to persist different objects.... via reflection? etc.
			setState(WaitingTaskState.getInstance(getClientAgentProxy()));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}
	
	private BasicDBObject summaryRequest(ServiceRequestFinished req) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.TotalTimeToServeRequest.toString());
		obj.append(EventType.TotalTimeToServeRequest.toString(), String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
		obj.append("factoryAgentName", req.getFactoryAgentName());
		return obj;
	}
	
	public static EngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, ExplorationResult selected) {
		return new EngagingInServiceComposition(clientAgentProxy, selected);
	}

}
