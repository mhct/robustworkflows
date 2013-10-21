package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

import be.kuleuven.robustworkflows.model.ModelStorageMap;

/**
 * Stores the data relative to the execution of a service composition
 * 
 * @author mario
 *
 */
public class ServiceCompositionSummaryEvent implements ModelEvent {
	
	
	private Map<String, Object> data;
	
	public ServiceCompositionSummaryEvent(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged) {
		
		data = new HashMap<String, Object>();
		data.put(ModelStorageMap.EXPECTED_TIME_TO_SERVE_COMPOSITION, expectedTimeToServeComposition);
		data.put(ModelStorageMap.REAL_TIME_TO_SERVE_COMPOSITION, realTimeToServeComposition);
		data.put(ModelStorageMap.CLIENT_AGENT, clientAgentName);
		data.put(ModelStorageMap.SERVICES_ENGAGED, servicesEngaged);
	}

	@Override
	public String eventType() {
		return ModelStorageMap.SERVICE_COMPOSITION_SUMMARY;
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
