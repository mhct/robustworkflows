package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

public class ExplorationReplyEvent implements ModelEvent {

	private static final String CLIENT_AGENT = "CLIENT_AGENT";
	private static final String FACTORY_AGENT = "FACTORY_AGENT";
	private static final String COMPUTATION_TIME = "COMPUTATION_TIME";
	
	private HashMap<String, Object> data;

	public ExplorationReplyEvent(String clientAgent,
			long computationTime,
			String factoryAgent) {
		
		data = new HashMap<String, Object>();
		data.put(CLIENT_AGENT, clientAgent);
		data.put(FACTORY_AGENT, factoryAgent);
		data.put(COMPUTATION_TIME, computationTime);
	}

	@Override
	public String eventType() {
		return EventType.ExplorationReply.toString();
	}

	@Override
	public Map<String, Object> values() {
		return data;
	}

	public static ModelEvent instance(ExplorationReply explorationReply,
			String factoryAgent) {

		return new ExplorationReplyEvent(explorationReply.getRequestExploration().getOriginName(),
				explorationReply.getComputationTime(),
				factoryAgent);
	}
}
