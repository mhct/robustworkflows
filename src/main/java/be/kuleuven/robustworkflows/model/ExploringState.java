package be.kuleuven.robustworkflows.model;

import akka.actor.ActorRef;

public class ExploringState extends ClientAgentState {

	public ExploringState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		// TODO Auto-generated method stub

	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		// TODO Auto-generated method stub
		return null;
	}

}
