package be.kuleuven.robustworkflows.model.clientagent;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;
import akka.actor.ActorRef;

/**
 * FIXME when an agent is engaging in a composition, it has to maintain information and engage with differet providers... 
 * better to create a more evolved set of abstractions to deal with that
 * 
 * FIXME currently it is only engaging with ServiceType A
 * 
 * @author mario
 *
 */
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
			selectedAgent.tell(ServiceRequest.getInstance(ServiceType.A), getClientAgentProxy().self());
			
		} else if (ServiceRequestFinished.class.isInstance(message)){
			persistEvent("ServiceComposition FINISHED");
			persistEvent(EventType.TotalTimeToServeRequest, String.valueOf(((ServiceRequestFinished) message).getServiceRequest().totalTimeToServeRequest()));
			//TODO add more information about this particular event.
			setState(WaitingTaskState.getInstance(getClientAgentProxy()));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}
	
	public static EngagingInServiceComposition getInstance(ClientAgentProxy clientAgentProxy, ActorRef selectedAgent) {
		return new EngagingInServiceComposition(clientAgentProxy, selectedAgent);
	}

}
