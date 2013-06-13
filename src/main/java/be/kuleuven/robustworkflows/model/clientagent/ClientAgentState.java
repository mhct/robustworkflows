package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;

public abstract class ClientAgentState {
	
	static final String RUN = "run";
	
	private ClientAgentProxy clientAgentProxy;

	public ClientAgentState(ClientAgentProxy clientAgentProxy) {
		this.clientAgentProxy = clientAgentProxy;
	}

	public abstract void onReceive(Object message, ActorRef actorRef) throws Exception;
	
	protected ClientAgentProxy getClientAgentProxy() {
		return clientAgentProxy;
	}
	
	protected void setState(ClientAgentState state) {
		getClientAgentProxy().setState(state);
	}
	
	protected void persistEvent(String event) {
		clientAgentProxy.getModelStorage().persistEvent(event);
	}
	
	protected void persistEvent(EventType eventType, String event) {
		clientAgentProxy.getModelStorage().persistEvent(eventType, event);
	}
	
	protected void addExpirationTimer(long time, String message) {
		clientAgentProxy.addExpirationTimer(time, message);
	}
}
