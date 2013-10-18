package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores the data relative to the execution of a service composition
 * 
 * @author mario
 *
 */
public class ServiceCompositionSummaryEvent implements ModelEvent {
	
	private static final String SERVICE_COMPOSITION_SUMMARY = "SERVICE_COMPOSITION_SUMMARY";
	private static final String EXPECTED_TIME_TO_SERVE_COMPOSITION = "EXPECTED_TIME_TO_SERVE_COMPOSITION";
	private static final String REAL_TIME_TO_SERVE_COMPOSITION = "REAL_TIME_TO_SERVE_COMPOSITION";
	private static final String CLIENT_AGENT = "CLIENT_AGENT";
	private static final String SERVICES_ENGAGED = "SERVICES_ENGAGED";
	
	private Map<String, Object> data;
	
	public ServiceCompositionSummaryEvent(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged) {
		
		data = new HashMap<String, Object>();
		data.put(EXPECTED_TIME_TO_SERVE_COMPOSITION, expectedTimeToServeComposition);
		data.put(REAL_TIME_TO_SERVE_COMPOSITION, realTimeToServeComposition);
		data.put(CLIENT_AGENT, clientAgentName);
		data.put(SERVICES_ENGAGED, servicesEngaged);
	}

	@Override
	public String eventType() {
		return SERVICE_COMPOSITION_SUMMARY;
	}

	@Override
	public Map<String, Object> values() {
		return new HashMap<String, Object>(data);
	}

	public static ServiceCompositionSummaryEvent instance(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged) {

		return new ServiceCompositionSummaryEvent(expectedTimeToServeComposition, realTimeToServeComposition, clientAgentName, servicesEngaged);
	}


}
