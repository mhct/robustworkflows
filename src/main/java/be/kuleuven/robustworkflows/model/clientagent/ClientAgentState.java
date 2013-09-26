package be.kuleuven.robustworkflows.model.clientagent;

import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.RequestExecutionData;

import com.mongodb.DBObject;

public abstract class ClientAgentState {
	
	public static final String RUN = "run";
	
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

	protected void persistEvent(DBObject event) {
		clientAgentProxy.getModelStorage().persistEvent(event);
	}
	
	protected void persistEvent(EventType eventType, String event) {
		clientAgentProxy.getModelStorage().persistEvent(eventType, event);
	}
	
	protected void addRequestExecutionData(RequestExecutionData requestData) {
		clientAgentProxy.addRequestsExecutionData(requestData);
	}
	
	protected List<RequestExecutionData> getRequestsData() {
		return clientAgentProxy.getRequestsData();
	}
	
	protected void clearRequests() {
		clientAgentProxy.clearRequestsData();
	}
	
	protected void addExpirationTimer(long time, String message) {
		clientAgentProxy.addExpirationTimer(time, message);
	}
}
