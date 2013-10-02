package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import java.util.Iterator;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

import com.mongodb.BasicDBObject;


public class RunningCompositionState extends ClientAgentState {
	private static final String EXPECTED_TIME_TO_SERVE_COMPOSITION = "EXPECTED_TIME_TO_SERVE_COMPOSITION";
	private static final String REAL_TIME_TO_SERVE_COMPOSITION = "REAL_TIME_TO_SERVE_COMPOSITION";
	
	private static ClientAgentState instance;
	private Iterator<WorkflowTask> tasksIterator;
	private long startTimeSelectedComposition;
	private boolean beginingComposition = true;
	
	private RunningCompositionState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
		tasksIterator = getClientAgentProxy().getWorkflow().iterator();
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			if (beginingComposition == true) {
				startTimeSelectedComposition = System.currentTimeMillis();
			}
			
			if (tasksIterator.hasNext()) {
				beginingComposition = false;
				WorkflowTask task = tasksIterator.next();
				setState(SimpleExploringState.getInstance(getClientAgentProxy(), task));
			}
			else {
				long realTimeToServeComposition = System.currentTimeMillis() - startTimeSelectedComposition;
				ServiceCompositionData serviceCompositionData = ServiceCompositionData.getInstance(getClientAgentProxy().clientAgentName(), getRequestsData(), realTimeToServeComposition);
				clearRequests();
				beginingComposition = true;
				persistEvent(summaryEngagement(serviceCompositionData));
				setState(SimpleWaitingTaskState.getInstance(getClientAgentProxy()));
			}
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	
	/**
	 * Creates a summary of the finished in a format accepted by the ModelStorage
	obj.append(REAL_TIME_TO_SERVE_REQUEST, String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
	 * 
	 * @param req
	 * @return
	 */
	private BasicDBObject summaryEngagement(ServiceCompositionData serviceCompositionData) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.SERVICE_COMPOSITION_SUMMARY.toString());
		
		obj.append(EXPECTED_TIME_TO_SERVE_COMPOSITION, serviceCompositionData.getExpectedTimeToServeComposition()); 
		obj.append(REAL_TIME_TO_SERVE_COMPOSITION, serviceCompositionData.getRealTimeToServeComposition());
		obj.append("CLIENT_AGENT", serviceCompositionData.getClientAgentName());
		obj.append("SERVICES_ENGAGED", serviceCompositionData.getServicesEngaged());
		
		return obj;
	}
	
	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		instance = new RunningCompositionState(clientAgentProxy);
		return instance;
	}
	
	public static ClientAgentState getActiveInstance(ClientAgentProxy clientAgentProxy) {
		if (instance == null) {
			throw new RuntimeException("RunningCompositionState is null, but it should not be null");
		} else {
			return instance;
		}
	}
}
