package be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior;

import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
