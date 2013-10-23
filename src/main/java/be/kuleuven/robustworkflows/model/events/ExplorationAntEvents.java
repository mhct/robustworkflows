package be.kuleuven.robustworkflows.model.events;

import java.util.Map;

import be.kuleuven.robustworkflows.model.ModelStorageMap;

import com.google.common.collect.Maps;

public class ExplorationAntEvents {

	public static ModelEvent timeout(final String clientAgent) {
		return new ModelEvent() {
			
			@Override
			public Map<String, Object> values() {
				Map<String, Object> map = Maps.newHashMap();
				map.put(ModelStorageMap.CLIENT_AGENT, clientAgent);
				return map;
			}
			
			@Override
			public String eventType() {
				return "EXPLORATION_ANT_TIMEOUT";
			}
		};
	}

	public static ModelEvent noReplies(final String clientAgent) {
		return new ModelEvent() {
			
			@Override
			public Map<String, Object> values() {
				Map<String, Object> map = Maps.newHashMap();
				map.put(ModelStorageMap.CLIENT_AGENT, clientAgent);
				return map;
			}
			
			@Override
			public String eventType() {
				return "EXPLORATION_ANT_NO_REPLIES";
			}
		};
	}
}
