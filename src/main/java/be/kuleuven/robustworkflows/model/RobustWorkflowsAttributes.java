package be.kuleuven.robustworkflows.model;

public class RobustWorkflowsAttributes {

	private final int startTimeInterval;
	private final int concurrentClients;

	private RobustWorkflowsAttributes(int startTimeInterval, int concurrentClients) {
		this.startTimeInterval = startTimeInterval;
		this.concurrentClients = concurrentClients;
	}
	
	public int getStartTimeInterval() {
		return startTimeInterval;
	}

	public int getConcurrentClients() {
		return concurrentClients;
	}
	
	public static RobustWorkflowsAttributes getInstance(int startTimeInterval, int concurrentClient) {
		return new RobustWorkflowsAttributes(startTimeInterval, concurrentClient);
	}
}
