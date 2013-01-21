package be.kuleuven.agent;

public class EventResponse {

	private EventRequest eventRequest;

	public EventResponse(EventRequest er) {
		this.eventRequest = er;
	}

	public static EventResponse from(EventRequest request) {
		EventResponse er = new EventResponse(request);
		return er;
	}
	
	public String toString() {
		return "RESPONSE: " + this.eventRequest.toString();
	}

}
