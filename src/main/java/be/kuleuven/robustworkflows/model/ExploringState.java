package be.kuleuven.robustworkflows.model;

import akka.actor.ActorRef;

public class ExploringState extends ClientAgentState {

	public ExploringState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (QoSData.class.isInstance(message)) {
			QoSData msg = (QoSData) message;
			System.out.println("Computation time, Agent:" + actorRef.path().toString() + ", computationTime" + msg.getComputationTime());
		}
	}

	public static ExploringState getInstance(ClientAgentProxy clientAgentProxy) {
		return new ExploringState(clientAgentProxy);
	}

}
