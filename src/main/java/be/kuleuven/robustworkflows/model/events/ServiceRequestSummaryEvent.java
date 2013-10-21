package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.robustworkflows.model.ModelStorageMap;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.RequestExecutionData;

/**
 * Stores the data relative to the execution of a Service Request
 * 
 * @author mario
 *
 */
public class ServiceRequestSummaryEvent implements ModelEvent {
	
	private Map<String, Object> data;
	
	public ServiceRequestSummaryEvent(long expectedTimeToExecuteTask,
			long realTimeToServeRequest,
			String factoryAgentName,
			String clientAgentName) {

		data = new HashMap<String, Object>();
		data.put(ModelStorageMap.EXPECTED_TIME_TO_SERVE_REQUEST, expectedTimeToExecuteTask);
		data.put(ModelStorageMap.REAL_TIME_TO_SERVE_REQUEST, realTimeToServeRequest);
		data.put(ModelStorageMap.FACTORY_AGENT, factoryAgentName);
		data.put(ModelStorageMap.CLIENT_AGENT, clientAgentName);
	}

	@Override
	public String eventType() {
		return ModelStorageMap.SERVICE_REQUEST_SUMMARY;
	}

	@Override
	public Map<String, Object> values() {
		return new HashMap<String, Object>(data);
	}

	public static ServiceRequestSummaryEvent instance(RequestExecutionData data) {
		return new ServiceRequestSummaryEvent(data.getExpectedTimeToExecuteTask(),
				data.getRealTimeToServeRequest(),
				data.getFactoryAgentName(),
				data.getClientAgentName());
	}


}
