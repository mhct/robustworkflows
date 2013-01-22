package be.kuleuven.agent;


/**
 * Request from the communication component to the Agent
 * 
 * @author mario
 *
 */
public class EventRequest {
	private final String originUri;
	private final EventPayload payload;
	
	private EventRequest(String originUri, EventPayload payload) {
		this.originUri = originUri;
		this.payload = payload;
	}

	public static EventRequest newInstance(String originUri, EventPayload payload) {
		EventRequest instance = new EventRequest(originUri, payload);
		return instance;
	}

	public EventPayload getPayload() {
		return payload;
	}
	
	public String getOriginUri() {
		return originUri;
	}
	
	public String toString() {
		return "REQUEST: origin: " + originUri + ", payload: " + this.payload; 
	}
}
