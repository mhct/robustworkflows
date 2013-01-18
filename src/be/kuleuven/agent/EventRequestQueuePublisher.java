package be.kuleuven.agent;

public interface EventRequestQueuePublisher {
	public void addRequest(Long id, Long payload);
}
