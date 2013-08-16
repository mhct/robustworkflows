package be.kuleuven.robustworkflows.model.clientagent;

import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;

public class ExploringState extends ClientAgentState {

	private final long EXPLORING_STATE_TIMEOUT_VALUE = 1100;
	private final String EXPLORING_STATE_TIMEOUT = "ExploringStateTimeout";
	private List<ExplorationResult> replies;
	
	public ExploringState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	public void run() {
		persistEvent("ExploringState: " + RUN);
		replies = Lists.newArrayList();
		getClientAgentProxy().getAntAPI().createExplorationAnt(getClientAgentProxy().getWorkflow());

		//TODO create new exploration ANTS NOW..? at at ClientAgent creation
		addExpirationTimer(EXPLORING_STATE_TIMEOUT_VALUE, EXPLORING_STATE_TIMEOUT);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			run();
		} else if (EXPLORING_STATE_TIMEOUT.equals(message)) {
			//TODO if there are no replies.... go back to another state, instead of SelectingComponentServices.
			persistEvent(EXPLORING_STATE_TIMEOUT);
			setState(SelectingComponentServices.getInstance(getClientAgentProxy(), replies));
			
		} else if (ExplorationResult.class.isInstance(message))  {
			ExplorationResult msg = (ExplorationResult) message;
			replies.add(msg);
			persistEvent(debugExplorationResult(msg));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
		
		//TODO add return type and with TRUE or FALSE for possible to handle of not.. the message think about this. 
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new ExploringState(clientAgentProxy);
	}
	
	private BasicDBObject debugExplorationResult(ExplorationResult msg) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("EventType", EventType.ExplorationResult.toString());
		
		obj.append("CLIENT_AGENT", getClientAgentProxy().self().path().name());
		obj.append("SERVICES_ENGAGED", msg.toString());
		
		return obj;
	}
}
