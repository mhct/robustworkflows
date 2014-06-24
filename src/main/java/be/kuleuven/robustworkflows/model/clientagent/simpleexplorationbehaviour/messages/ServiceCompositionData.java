package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages;

import java.io.Serializable;
import java.util.List;

public class ServiceCompositionData implements Serializable {

	private static final long serialVersionUID = 24092013L;
	private final String clientAgentName;
	private final String servicesEngaged;
	private final long expectedTimeToServeComposition;
	private final long realTimeToServeComposition;

	public ServiceCompositionData(String clientAgentName,
			String servicesEngaged, long expectedTimeToServeComposition,
			long realTimeToServeComposition) {
		
		this.clientAgentName = clientAgentName;
		this.servicesEngaged = servicesEngaged;
		this.expectedTimeToServeComposition = expectedTimeToServeComposition;
		this.realTimeToServeComposition = realTimeToServeComposition;
	}
	

	public String getClientAgentName() {
		return clientAgentName;
	}


	public String getServicesEngaged() {
		return servicesEngaged;
	}


	public long getExpectedTimeToServeComposition() {
		return expectedTimeToServeComposition;
	}


	public long getRealTimeToServeComposition() {
		return realTimeToServeComposition;
	}


	public static ServiceCompositionData getInstance(
			String clientAgentName,
			List<RequestExecutionData> requestsData,
			long realTimeToServeComposition) {
		
		long expectedTimeToServeComposition = 0;
		String servicesEngaged = "";
		for (RequestExecutionData r: requestsData) {
			expectedTimeToServeComposition += r.getExpectedTimeToExecuteTask();
			servicesEngaged += ";" + r.getFactoryAgentName();
		}
		
		return new ServiceCompositionData(clientAgentName, servicesEngaged, expectedTimeToServeComposition, realTimeToServeComposition);
	}

}
