package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;


public class WaitingTaskState extends ClientAgentState {
//	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public WaitingTaskState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			persistEvent("WaitingTaskState: " + RUN);
			
		} else if ("Compose".equals(message)) {
			persistEvent("Received a compose message");
			setState(ExploringState.getInstance(getClientAgentProxy()));
			
		}
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new WaitingTaskState(clientAgentProxy);
	}
}
