package be.kuleuven.robustworkflows.model.events;

import java.util.Map;

/**
 * Represents and Event relevant to the Model execution, that should be persisted for later analysis
 * 
 * @author mario
 *
 */
public interface ModelEvent {
	String eventType();
	Map<String, Object> values();
}
