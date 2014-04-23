package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.teradata.jdbc.jdbc_4.logging.Log;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ant.ExploreService;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

/**
 * Simple Exploration State. This implementation has only two distinct states SimpleExploringState <--> SimpleEngagementState
 * 
 *
 * === Inbound Messages ===
 * - ''' SimpleExplorationResult ''' the best possible Agent to be engaged
 * - ''' EXPLORATION_TIMEOUT '''
 * 
 * === Outbound Messages ===
 * - ''' ExploreService ''' tell ExplorationAnts to explore the environment, for services of the required WorkflowTask Type
 * 
 * @author mario
 *
 */
public class SimpleExploringState extends ClientAgentState {

	private static final String EXPLORATION_TIMEOUT = "EXPLORATION_TIMEOUT";
	private WorkflowTask workflowTask;
	private List<SimpleExplorationResult> results = new ArrayList<SimpleExplorationResult>();

	@Override
	public void run() {
		getClientAgentProxy().getAntAPI().tellAll(ExploreService.type(workflowTask.getType()));
		getClientAgentProxy().addExpirationTimer(getClientAgentProxy().getAttributes().getExplorationStateTimeout(), SimpleExploringState.EXPLORATION_TIMEOUT);
	}
	
	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (SimpleExplorationResult.class.isInstance(message)) {
			getClientAgentProxy().getLoggingAdapter().debug("ClientAgent" + getClientAgentProxy().clientAgentName() + ": SimpleExplorationResult received.");
			
			SimpleExplorationResult msg = (SimpleExplorationResult) message;
			results.add(msg);
		} else if (EXPLORATION_TIMEOUT.equals(message)) {
			SimpleExplorationResult selectedExplorationResult = selectLowerExplorationResult();
			
			if (selectedExplorationResult != null && !selectedExplorationResult.isEmpty()) {
				getClientAgentProxy().setState(SimpleEngagingInServiceComposition.getInstance(getClientAgentProxy(), selectedExplorationResult));
			} else {
				//FIXME should not use this persistEvent(String) method anymore...
//				persistEvent("ClientAgent" + getClientAgentProxy().clientAgentName() + " .could not find suitable services...");
				//TODO go back to RunningCompositionState
				getClientAgentProxy().getLoggingAdapter().debug("ClientAgent" + getClientAgentProxy().clientAgentName() + " .could not find suitable services...");
			}
			
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	private SimpleExplorationResult selectLowerExplorationResult() {
		Collections.sort(results, new Comparator<SimpleExplorationResult>() {

			@Override
			public int compare(SimpleExplorationResult obj1, SimpleExplorationResult obj2) {
				final long o1 = obj1.computationTime();
				final long o2 = obj2.computationTime();
				
				if (o1 < o2) {
					return -1;
				} else if (o1 > o2) {
					return 1;
				}
					
				return 0;
			}
			
		});
		
		SimpleExplorationResult selectedExplorationResult = null;
		
		if (results.size() >= 1) {
			selectedExplorationResult = results.get(0);
			results.clear();
		}			
		
		return selectedExplorationResult;
	}
	
	public SimpleExploringState(ClientAgentProxy clientAgentProxy, WorkflowTask workflowTask) {
		super(clientAgentProxy);
		this.workflowTask = workflowTask;
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy, WorkflowTask workflowTask) {
		return new SimpleExploringState(clientAgentProxy, workflowTask);
	}


}
