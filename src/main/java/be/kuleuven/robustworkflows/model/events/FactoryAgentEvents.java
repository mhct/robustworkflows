package be.kuleuven.robustworkflows.model.events;

import java.util.Map;

public class FactoryAgentEvents {

	public static ModelEvent TIME_TO_WORK_FOR_REQUEST_FINISHED() {
		return new ModelEvent() {
			
			@Override
			public Map<String, Object> values() {
				return null;
			}
			
			@Override
			public String eventType() {
				return "TIME_TO_WORK_FOR_REQUEST_FINISHED";
			}
		};
	}

	
}
