package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;

public class ChoosingPartnersState extends ClientAgentState {

	private ChoosingPartnersState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		// do nothing
	}
	
	public static ChoosingPartnersState getInstance(ClientAgentProxy clientAgentProxy) {
		return new ChoosingPartnersState(clientAgentProxy);
	}

	public void run() {
		
	}

}
