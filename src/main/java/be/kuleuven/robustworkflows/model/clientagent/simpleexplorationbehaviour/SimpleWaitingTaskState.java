package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;


public class SimpleWaitingTaskState extends ClientAgentState {

//	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private SimpleWaitingTaskState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			persistEvent("WaitingTaskState: " + RUN);
			
		} else if (COMPOSE.equals(message)) {
			setState(RunningCompositionState.getInstance(getClientAgentProxy()));
			
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new SimpleWaitingTaskState(clientAgentProxy);
	}
}
