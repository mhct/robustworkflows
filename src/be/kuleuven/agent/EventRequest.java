package be.kuleuven.agent;


/**
 * Request from the communication component to the Agent
 * 
 * @author mario
 *
 */
public class EventRequest {
	private Long id;
	private Long payload; //payload could even be a java method, or a command
	
	private EventRequest(Long id, Long payload) {
		this.id = id;
		this.payload = payload;
	}

	public static EventRequest newInstance(Long id, Long payload) {
		EventRequest instance = new EventRequest(id, payload);
		return instance;
	}

	public Long getId() {
		return id;
	}

	public Long getPayload() {
		return payload;
	}
	
	public String toString() {
		return "REQUEST: id: " + this.id + ", payload: " + this.payload; 
	}
}
