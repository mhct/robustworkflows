package be.kuleuven.robustworkflows.model.clientagent;

import java.util.Map;

import be.kuleuven.robustworkflows.model.messages.QoSData;
import akka.actor.ActorRef;

public class SelectingComponentServices extends ClientAgentState {

	private Map<ActorRef, QoSData> replies;

	private SelectingComponentServices(ClientAgentProxy clientAgentProxy, Map<ActorRef, QoSData> replies) {
		super(clientAgentProxy);
		
		this.replies = replies;
	}
	
	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			persistEvent("SelectingComponentServices");
			ActorRef selected = getClientAgentProxy().evaluateComposition(replies);
			setState(EngagingInServiceComposition.getInstance(getClientAgentProxy(), selected));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}
	
	public static SelectingComponentServices getInstance(ClientAgentProxy clientAgentProxy, Map<ActorRef, QoSData> replies) {
		return new SelectingComponentServices(clientAgentProxy, replies);
	}

}
