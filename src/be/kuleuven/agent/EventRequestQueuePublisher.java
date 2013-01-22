package be.kuleuven.agent;

public interface EventRequestQueuePublisher {
	public void addRequest(String originUri, EventPayload payload);
}
