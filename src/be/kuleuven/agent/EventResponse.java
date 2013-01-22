package be.kuleuven.agent;

public class EventResponse {
	private final String destination;
	private final EventPayload payload;

	private EventResponse(String destination, EventPayload payload) {
		this.destination = destination;
		this.payload = payload;
	}

	public String getDestination() {
		return destination;
	}
	
	public EventPayload getPayload() {
		return payload;
	}
	
	public static EventResponse from(EventRequest request) {
		EventResponse er = new EventResponse(request.getOriginUri(), new EventPayload() {
		});
		
		return er;
	}

	public static EventResponse to(String destination, ProductionQuoteReply quote) {
		EventResponse er = new EventResponse(destination, quote);
		
		return er;
	}

	public String toString() {
		return "RESPONSE: " + destination + " payload" + payload.toString();
	}
}
