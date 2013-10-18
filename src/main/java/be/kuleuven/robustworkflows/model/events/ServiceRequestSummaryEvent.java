package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.RequestExecutionData;

/**
 * Stores the data relative to the execution of a Service Request
 * 
 * @author mario
 *
 */
public class ServiceRequestSummaryEvent implements ModelEvent {
	private static final String SERVICE_REQUEST_SUMMARY = "SERVICE_REQUEST_SUMMARY";
	private static final String EXPECTED_TIME_TO_SERVE_REQUEST = "EXPECTED_TIME_TO_SERVE_REQUEST";
	private static final String REAL_TIME_TO_SERVE_REQUEST = "REAL_TIME_TO_SERVE_REQUEST";
	private static final String CLIENT_AGENT = "CLIENT_AGENT";
	private static final String FACTORY_AGENT = "FACTORY_AGENT";
	
	private Map<String, Object> data;
	
	public ServiceRequestSummaryEvent(long expectedTimeToExecuteTask,
			long realTimeToServeRequest,
			String factoryAgentName,
			String clientAgentName) {

		data = new HashMap<String, Object>();
		data.put(EXPECTED_TIME_TO_SERVE_REQUEST, expectedTimeToExecuteTask);
		data.put(REAL_TIME_TO_SERVE_REQUEST, realTimeToServeRequest);
		data.put(FACTORY_AGENT, factoryAgentName);
		data.put(CLIENT_AGENT, clientAgentName);
	}

	@Override
	public String eventType() {
		return SERVICE_REQUEST_SUMMARY;
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
