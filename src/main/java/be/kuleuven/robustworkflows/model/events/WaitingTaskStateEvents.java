package be.kuleuven.robustworkflows.model.events;

import java.util.Map;

public class WaitingTaskStateEvents implements ModelEvent {

	private final String EVENT_TYPE = "WaitingTask";
	
	@Override
	public String eventType() {
		return EVENT_TYPE;
	}

	@Override
	public Map<String, Object> values() {
		return null;
	}
	
	private WaitingTaskStateEvents() {
		
	}
	
	public static WaitingTaskStateEvents instance() {
		return new WaitingTaskStateEvents();
	}

}
