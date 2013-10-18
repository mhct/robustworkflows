package be.kuleuven.robustworkflows.model.events;

import java.util.Map;

public class ExplorationAntEvents {

	public static ModelEvent timeout() {
		return new ModelEvent() {
			
			@Override
			public Map<String, Object> values() {
				return null;
			}
			
			@Override
			public String eventType() {
				return "EXPLORATION_ANT_TIMEOUT";
			}
		};
	}

	public static ModelEvent noReplies() {
		return new ModelEvent() {
			
			@Override
			public Map<String, Object> values() {
				return null;
			}
			
			@Override
			public String eventType() {
				return "EXPLORATION_ANT_NO_REPLIES";
			}
		};
	}
}
