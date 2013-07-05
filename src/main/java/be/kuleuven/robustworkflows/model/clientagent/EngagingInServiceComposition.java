package be.kuleuven.robustworkflows.model.clientagent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;
import akka.actor.ActorRef;

/**
 * FIXME when an agent is engaging in a composition, it has to maintain information and engage with differet providers... 
 * better to create a more evolved set of abstractions to deal with that
 * 
 * FIXME currently it is only engaging with ServiceType A
 * TODO check ExplorationResult
 * @author mario
 *
 */
public class EngagingInServiceComposition extends ClientAgentState {

	private ExplorationResult selectedComposition;
	private int serviceRequestCounter = 0;
	private HashMap<Integer, ServiceRequestExplorationReply> replies; 
	
	private EngagingInServiceComposition(ClientAgentProxy clientAgentProxy, ExplorationResult selected) {
		super(clientAgentProxy);
		this.selectedComposition = selected;
		this.replies = Maps.newHashMap();
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			//FIXME this is a hack. create new abstraction to perform this operation. (IntentionAnts)
			for (WorkflowTask t: selectedComposition) {
				persistEvent("Engaging in Service Composition: " + t.getAgent().path().name()); //FIXME add ClientAgent name
				serviceRequestCounter = serviceRequestCounter + 1;
				t.getAgent().tell(ServiceRequest.getInstance(serviceRequestCounter, t.getType()), getClientAgentProxy().self());
				replies.put(serviceRequestCounter, t.getQoS());
				
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
	
	/**
	 * Creates a summary of the finished in a format accepted by the ModelStorage
	 * 
	 * @param req
	 * @return
	 */
	private BasicDBObject summaryRequest(ServiceRequestFinished req) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.ServiceRequestSummary.toString());
		
		obj.append(EventType.ExpectedTimeToServeRequest.toString(), replies.get(req.getServiceRequest().getId()).getComputationTime()); 
		obj.append(EventType.TotalTimeToServeRequest.toString(), String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
		obj.append("factoryAgentName", req.getFactoryAgentName());
		return obj;
	}
	
	public static EngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, ExplorationResult selected) {
		return new EngagingInServiceComposition(clientAgentProxy, selected);
	}

}
