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
	private Iterator<WorkflowTask> itr;
	private long startTimeSelectedComposition;
	private String servicesEngaged = "";

	private RunningCompositionState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
		itr = getClientAgentProxy().getWorkflow().iterator();
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			startTimeSelectedComposition = System.currentTimeMillis();
			
			if (itr.hasNext()) {
//				servicesEngaged += ";" + task.getAgent().path().name();
				setState(SimpleExploringState.getInstance(getClientAgentProxy(), itr.next()));
			}
			else {
				persistEvent(summaryEngagement());
				setState(WaitingTaskState.getInstance(getClientAgentProxy()));
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
	private BasicDBObject summaryEngagement() {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.SERVICE_COMPOSITION_SUMMARY.toString());
		
		obj.append(EXPECTED_TIME_TO_SERVE_COMPOSITION, String.valueOf(getClientAgentProxy().getWorkflow().totalComputationTime())); 
		obj.append(REAL_TIME_TO_SERVE_COMPOSITION, String.valueOf(System.currentTimeMillis() - startTimeSelectedComposition));
		obj.append("CLIENT_AGENT", getClientAgentProxy().self().path().name());
		obj.append("SERVICES_ENGAGED", servicesEngaged);
		
		return obj;
	}
	
	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		if (instance == null) {
			instance = new RunningCompositionState(clientAgentProxy);
			return instance;
		}
		else {
			return instance;
		}
	}
}
