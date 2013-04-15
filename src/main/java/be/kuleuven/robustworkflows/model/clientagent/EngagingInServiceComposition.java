package be.kuleuven.robustworkflows.model.clientagent;

import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;
import akka.actor.ActorRef;

public class EngagingInServiceComposition extends ClientAgentState {

	private ActorRef selectedAgent;

	private EngagingInServiceComposition(ClientAgentProxy clientAgentProxy, ActorRef selectedAgent) {
		super(clientAgentProxy);
		this.selectedAgent = selectedAgent;
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			persistEvent("Engaging in Service Composition: Agent " + selectedAgent.path().name());
			selectedAgent.tell(ServiceRequest.getInstance(), getClientAgentProxy().self());
		} else if (ServiceRequestFinished.class.isInstance(message)){
			persistEvent("ServiceComposition FINISHED");
			setState(WaitingTaskState.getInstance(getClientAgentProxy()));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}
	
	public static EngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, ActorRef selectedAgent) {
		return new EngagingInServiceComposition(clientAgentProxy, selectedAgent);
	}

}
