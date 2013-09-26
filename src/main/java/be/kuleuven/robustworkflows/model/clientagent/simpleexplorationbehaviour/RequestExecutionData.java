package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import java.io.Serializable;

public class RequestExecutionData implements Serializable {

	private static final long serialVersionUID = 24092013L;
	private final String clientAgentName;
	private final String factoryAgentName;
	private final long expectedTimeToExecuteTask;
	private final long realTimeToServeRequest;

	
	public String getClientAgentName() {
		return clientAgentName;
	}



	public String getFactoryAgentName() {
		return factoryAgentName;
	}



	public long getExpectedTimeToExecuteTask() {
		return expectedTimeToExecuteTask;
	}



	public long getRealTimeToServeRequest() {
		return realTimeToServeRequest;
	}

	private RequestExecutionData(String clientAgentName,
			String factoryAgentName, long expectedTimeToExecuteTask,
			long realTimeToServeRequest) {
		this.clientAgentName = clientAgentName;
		this.factoryAgentName = factoryAgentName;
		this.expectedTimeToExecuteTask = expectedTimeToExecuteTask;
		this.realTimeToServeRequest = realTimeToServeRequest;
	}

	public static RequestExecutionData getInstance(String clientAgentName,
			String factoryAgentName, long expectedTimeToExecuteTask,
			long realTimeToServeRequest) {
		return new RequestExecutionData(clientAgentName, factoryAgentName, expectedTimeToExecuteTask, realTimeToServeRequest);
	}

}
