package be.kuleuven.agent;

public class AssemblyAgent extends Agent {

	AssemblyAgent(String name, EventRequestQueueReader requestsQueue, EventResponseQueuePublisher responseQueue) {
		super("aa", null, null);
	}
	
	public static AssemblyAgent newInstance(String name, EventRequestQueueReader requestsQueue, EventResponseQueuePublisher responseQueue) {
		AssemblyAgent aa = new AssemblyAgent(name, requestsQueue, responseQueue);
		return aa;
	}
}
