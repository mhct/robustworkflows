package be.kuleuven.robustworkflows.model.clientagent;

import java.util.Iterator;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

import com.mongodb.BasicDBObject;

/**
 * Currently it will iterate SEQUENTIALLY unregarding if the workflow can have parallel tasks.
 * 
 * FIXME when an agent is engaging in a composition, it has to maintain information and engage with different providers... 
 * better to create a more evolved set of abstractions to deal with that
 * 
 * TODO check ExplorationResult
 * 
 * @author mario
 *
 */
public class EngagingInServiceComposition extends ClientAgentState {
	private static final String EXPECTED_TIME_TO_SERVE_COMPOSITION = "EXPECTED_TIME_TO_SERVE_COMPOSITION";
	private static final String REAL_TIME_TO_SERVE_COMPOSITION = "REAL_TIME_TO_SERVE_COMPOSITION";
	private static final String EXPECTED_TIME_TO_SERVE_REQUEST = "EXPECTED_TIME_TO_SERVE_REQUEST";
	private static final String REAL_TIME_TO_SERVE_REQUEST = "REAL_TIME_TO_SERVE_REQUEST"; 
	
	private Workflow selectedComposition;
	private int serviceRequestCounter = 0;
	private WorkflowTask currentTask;
	private long startTimeSelectedComposition;
	private long startTimeCurrentTask;
	private Iterator<WorkflowTask> itr; 
	private String servicesEngaged = ""; // Maintain a list of engaged services
	
	private EngagingInServiceComposition(ClientAgentProxy clientAgentProxy, Workflow selectedComposition) {
		super(clientAgentProxy);
		this.selectedComposition = selectedComposition;
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			persistEvent("Engaging in Service Composition: " + selectedComposition.toString()); // + t.getAgent().path().name()); //FIXME add ClientAgent name

			startTimeSelectedComposition = System.currentTimeMillis();
			servicesEngaged = "";
			itr = selectedComposition.iterator();
			engageWithServiceProvider();
		} 
		else if (ServiceRequestFinished.class.isInstance(message)){
			final ServiceRequestFinished msg = (ServiceRequestFinished) message;
			persistEvent(summaryServiceRequest(msg));
			servicesEngaged += ";" + msg.getFactoryAgentName();
			
			if (!itr.hasNext()) {
				persistEvent("ServiceComposition FINISHED");
				persistEvent(summaryEngagement());
				//TODO add more information about this particular event.
				//TODO, look a better way to persist different objects.... via reflection? etc.
				setState(WaitingTaskState.getInstance(getClientAgentProxy()));
//				setState(ExploringState.getInstance(getClientAgentProxy()));
			} else {
				engageWithServiceProvider();
			}
			
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	private void engageWithServiceProvider() {
		currentTask = itr.next();
		startTimeCurrentTask = System.currentTimeMillis();
		serviceRequestCounter = serviceRequestCounter + 1;
		
		currentTask.getAgent().tell(ServiceRequest.getInstance(serviceRequestCounter, currentTask.getType()), getClientAgentProxy().self());
	}
	
	/**
	 * Creates a summary of the finished in a format accepted by the ModelStorage
	obj.append(REAL_TIME_TO_SERVE_REQUEST, String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
	 * 
	 * @param req
	 * @return
	 */
	private BasicDBObject summaryServiceRequest(ServiceRequestFinished req) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.SERVICE_REQUEST_SUMMARY.toString());
		
		obj.append(EXPECTED_TIME_TO_SERVE_REQUEST, String.valueOf(currentTask.getQoS().getComputationTime())); 
		obj.append(REAL_TIME_TO_SERVE_REQUEST, String.valueOf(System.currentTimeMillis() - startTimeCurrentTask));
		obj.append("factoryAgentName", req.getFactoryAgentName());
		return obj;
	}

	/**
	 * Creates a summary of the finished in a format accepted by the ModelStorage
	obj.append(REAL_TIME_TO_SERVE_REQUEST, String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
	 * 
	 * @param req
	 * @return
	 */
	private BasicDBObject summaryEngagement() {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.SERVICE_COMPOSITION_SUMMARY.toString());
		
		obj.append(EXPECTED_TIME_TO_SERVE_COMPOSITION, String.valueOf(selectedComposition.totalComputationTime())); 
		obj.append(REAL_TIME_TO_SERVE_COMPOSITION, String.valueOf(System.currentTimeMillis() - startTimeSelectedComposition));
		obj.append("CLIENT_AGENT", getClientAgentProxy().self().path().name());
		obj.append("SERVICES_ENGAGED", servicesEngaged);
		
		return obj;
	}
	
	public static EngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, Workflow selected) {
		return new EngagingInServiceComposition(clientAgentProxy, selected);
	}

}
