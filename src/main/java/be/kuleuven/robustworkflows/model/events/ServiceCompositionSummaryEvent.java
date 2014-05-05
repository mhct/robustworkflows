package be.kuleuven.robustworkflows.model.events;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import be.kuleuven.robustworkflows.model.ModelStorageMap;

/**
 * Stores the data relative to the execution of a service composition
 * 
 * @author mario
 *
 */
public class ServiceCompositionSummaryEvent implements ModelEvent {
	
	
	private Map<String, Object> data;
	private String time;
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	
	public ServiceCompositionSummaryEvent(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged) {
		
		data = new HashMap<String, Object>();
		data.put(ModelStorageMap.EXPECTED_TIME_TO_SERVE_COMPOSITION, expectedTimeToServeComposition);
		data.put(ModelStorageMap.REAL_TIME_TO_SERVE_COMPOSITION, realTimeToServeComposition);
		data.put(ModelStorageMap.CLIENT_AGENT, clientAgentName);
		data.put(ModelStorageMap.SERVICES_ENGAGED, servicesEngaged);
		data.put(ModelStorageMap.TIME_BLOCK, dtf.print(new DateTime()));
	}

	@Override
	public String eventType() {
		return ModelStorageMap.SERVICE_COMPOSITION_SUMMARY;
	}

	@Override
	public Map<String, Object> values() {
		return new HashMap<String, Object>(data);
	}

	
	@Override
	public String toString() {
		return "ServiceCompositionSummaryEvent [data=" + data
				+ ", eventType()=" + eventType() + ", values()=" + values()
				+ "]";
	}

	public static ServiceCompositionSummaryEvent instance(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged) {

		return new ServiceCompositionSummaryEvent(expectedTimeToServeComposition, realTimeToServeComposition, clientAgentName, servicesEngaged);
	}

}
