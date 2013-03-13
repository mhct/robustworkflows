package be.kuleuven.robustworkflows.model;

import akka.actor.ActorRef;


public class WaitingTaskState extends ClientAgentState {
//	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	public WaitingTaskState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if ("Compose".equals(message)) {
			System.out.println("Received a compose message");
			getClientAgentProxy().broadcastToNeighbors(ServiceRequestExploration.getInstance("A", 1, getClientAgentProxy().self())); //TODO add workflow to agent
			setState(ExploringState.getInstance(getClientAgentProxy()));
		}
		
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new WaitingTaskState(clientAgentProxy);
	}


}
