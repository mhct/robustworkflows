package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages;

import java.io.Serializable;

public class RequestExecutionData implements Serializable {

	private static final long serialVersionUID = 24092013L;
	private final String clientAgentName;
	private final String factoryAgentName;
	private final long expectedTimeToExecuteTask;
	private final long realTimeToExecuteTask;

	
	public String getClientAgentName() {
		return clientAgentName;
	}



	public String getFactoryAgentName() {
		return factoryAgentName;
	}



	public long getExpectedTimeToExecuteTask() {
		return expectedTimeToExecuteTask;
	}



	public long getRealTimeToExecuteTask() {
		return realTimeToExecuteTask;
	}

	private RequestExecutionData(String clientAgentName,
			String factoryAgentName, long expectedTimeToExecuteTask,
			long realTimeToServeRequest) {
		this.clientAgentName = clientAgentName;
		this.factoryAgentName = factoryAgentName;
		this.expectedTimeToExecuteTask = expectedTimeToExecuteTask;
		this.realTimeToExecuteTask = realTimeToServeRequest;
	}

	public static RequestExecutionData getInstance(String clientAgentName,
			String factoryAgentName, long expectedTimeToExecuteTask,
			long realTimeToServeRequest) {
		return new RequestExecutionData(clientAgentName, factoryAgentName, expectedTimeToExecuteTask, realTimeToServeRequest);
	}

}
