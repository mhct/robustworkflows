package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.robustworkflows.model.ModelStorageMap;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

public class ExplorationReplyEvent implements ModelEvent {

	
	private HashMap<String, Object> data;

	public ExplorationReplyEvent(String clientAgent,
			long computationTime,
			String factoryAgent,
			long explorationrequestId) {
		
		data = new HashMap<String, Object>();
		data.put(ModelStorageMap.CLIENT_AGENT, clientAgent);
		data.put(ModelStorageMap.FACTORY_AGENT, factoryAgent);
		data.put(ModelStorageMap.COMPUTATION_TIME, computationTime);
		data.put(ModelStorageMap.EXPLORATION_REQUEST_ID, explorationrequestId);
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
				factoryAgent,
				explorationReply.getRequestExploration().getId());
	}
}
