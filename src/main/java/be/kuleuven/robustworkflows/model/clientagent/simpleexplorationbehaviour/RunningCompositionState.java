package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import java.util.Iterator;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;


public class RunningCompositionState extends ClientAgentState {
private static ClientAgentState instance;
private Iterator<WorkflowTask> itr;

	private RunningCompositionState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
		itr = getClientAgentProxy().getWorkflow().iterator();
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
//			persistEvent("WaitingTaskState: " + RUN);
			
			if (itr.hasNext()) {
				setState(SimpleExploringState.getInstance(getClientAgentProxy(), itr.next()));
			}
			else {
				setState(WaitingTaskState.getInstance(getClientAgentProxy()));
			}
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
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
