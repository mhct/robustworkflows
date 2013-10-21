package be.kuleuven.robustworkflows.model.events;

import java.util.Map;

public class ComposeEvent implements ModelEvent {

	@Override
	public String eventType() {
		return "Compose";
	}

	@Override
	public Map<String, Object> values() {
		return null;
	}

	public static ModelEvent getInstance() {
		return new ComposeEvent();
	}

}
