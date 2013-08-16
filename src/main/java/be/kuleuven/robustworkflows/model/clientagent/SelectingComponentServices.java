package be.kuleuven.robustworkflows.model.clientagent;

import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;

//FIXME perhaps I don't really need to model this state as a class
public class SelectingComponentServices extends ClientAgentState {

	private final List<ExplorationResult> replies;

	private SelectingComponentServices(ClientAgentProxy clientAgentProxy, List<ExplorationResult> replies) {
		super(clientAgentProxy);
		
		this.replies = replies;
	}
	
	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			
			persistEvent("BEFORE replies.size(): " + replies.size() + "\t" + (replies !=null? replies: "REPLIES WAS NULL"));
			
			ExplorationResult selected = getClientAgentProxy().evaluateComposition(replies);
			persistEvent("SelectingComponentServices. selected. " + selected.toString());
			setState(EngagingInServiceComposition.getInstance(getClientAgentProxy(), selected.getWorkflow()));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}
	
	public static SelectingComponentServices getInstance(ClientAgentProxy clientAgentProxy, List<ExplorationResult> replies) {
		return new SelectingComponentServices(clientAgentProxy, replies);
	}

}
