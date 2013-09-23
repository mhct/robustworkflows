package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;


public class WaitingTaskState extends ClientAgentState {

//	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private WaitingTaskState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			persistEvent("WaitingTaskState: " + RUN);
			
		} else if ("Compose".equals(message)) {
			persistEvent("Received a compose message");
			
			setState(RunningCompositionState.getInstance(getClientAgentProxy()));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new WaitingTaskState(clientAgentProxy);
	}
}
