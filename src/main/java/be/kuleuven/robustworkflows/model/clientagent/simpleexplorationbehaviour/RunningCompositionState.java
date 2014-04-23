package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import java.util.Iterator;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.events.ServiceCompositionSummaryEvent;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

/**
 * 
 * === Inbound Mesages ===
 * - NO MESSAGES
 * 
 * === Outbound Messages ===
 * - NO MESSAGES
 * 
 * @author mario
 *
 */
public class RunningCompositionState extends ClientAgentState {

	private Iterator<WorkflowTask> tasksIterator;
	private long startTimeSelectedComposition;
	private boolean beginingComposition = true;
	
	private RunningCompositionState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
		tasksIterator = getClientAgentProxy().getWorkflow().iterator();
	}

	@Override
	public void run() {
		if (beginingComposition == true) {
			startTimeSelectedComposition = System.currentTimeMillis();
		}
		if (tasksIterator.hasNext()) {
			beginingComposition = false;
			WorkflowTask task = tasksIterator.next();
			getClientAgentProxy().getLoggingAdapter().debug("ClientAgentName: " + getClientAgentProxy().clientAgentName() + " task: " + task);
			setState(SimpleExploringState.getInstance(getClientAgentProxy(), task));
		}
		else {
			//
			// completed the execution of a composition
			//
			long realTimeToServeComposition = System.currentTimeMillis() - startTimeSelectedComposition;
			ServiceCompositionData serviceCompositionData = ServiceCompositionData.getInstance(getClientAgentProxy().clientAgentName(), getRequestsData(), realTimeToServeComposition);
			clearRequests();
			beginingComposition = true;
			getClientAgentProxy().getLoggingAdapter().info("Completed the execution of a composition");
			persistEvent(summaryEngagement(serviceCompositionData));
			getClientAgentProxy().getModelStorage().persistWriteCache();
			
			setState(SimpleWaitingTaskState.getInstance(getClientAgentProxy()));
		}
	}
	
	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		getClientAgentProxy().unhandledMessage(message);
	}

	
	/**
	 * Creates a summary of the finished in a format accepted by the ModelStorage
	obj.append(REAL_TIME_TO_SERVE_REQUEST, String.valueOf(req.getServiceRequest().totalTimeToServeRequest()));
	 * 
	 * @param req
	 * @return
	 */
	private ServiceCompositionSummaryEvent summaryEngagement(ServiceCompositionData serviceCompositionData) {
		
		return ServiceCompositionSummaryEvent.instance(serviceCompositionData.getExpectedTimeToServeComposition(),
				serviceCompositionData.getRealTimeToServeComposition(),
				serviceCompositionData.getClientAgentName(),
				serviceCompositionData.getServicesEngaged());
	}
	
	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new RunningCompositionState(clientAgentProxy);
	}
}
