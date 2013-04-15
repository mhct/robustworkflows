package be.kuleuven.robustworkflows.model.clientagent;

import java.util.Map;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.QoSData;

import com.google.common.collect.Maps;

public class ExploringState extends ClientAgentState {
	
	private final String EXPLORING_STATE_TIMEOUT = "ExploringStateTimeout";
	Map<ActorRef, QoSData> replies = Maps.newHashMap(); //replies received from other agents
	
	public ExploringState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	public void run() {
		persistEvent("ExploringState: " + RUN);
		getClientAgentProxy().broadcastToNeighbors(getClientAgentProxy().getWorkflow());
		addExpirationTimer(100, EXPLORING_STATE_TIMEOUT);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			run();
		} else if (EXPLORING_STATE_TIMEOUT.equals(message)) {
			persistEvent(EXPLORING_STATE_TIMEOUT);
			for (Map.Entry<ActorRef, QoSData> entry : replies.entrySet()) {
				persistEvent(entry.getKey().toString() + ": " + entry.getValue().toString());
			}
			setState(SelectingComponentServices.getInstance(getClientAgentProxy(), replies));
			
		} else if (QoSData.class.isInstance(message)) {
			QoSData msg = (QoSData) message;
			replies.put(actorRef, msg);
			
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
		
		//TODO add return type and with TRUE or FALSE for possible to handle of not.. the message 
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new ExploringState(clientAgentProxy);
	}

}
