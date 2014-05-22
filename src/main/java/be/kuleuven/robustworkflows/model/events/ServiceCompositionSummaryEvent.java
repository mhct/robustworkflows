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
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	
	public ServiceCompositionSummaryEvent(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged,
			DateTime startTime) {
		
		data = new HashMap<String, Object>();
		data.put(ModelStorageMap.EXPECTED_TIME_TO_SERVE_COMPOSITION, expectedTimeToServeComposition);
		data.put(ModelStorageMap.REAL_TIME_TO_SERVE_COMPOSITION, realTimeToServeComposition);
		data.put(ModelStorageMap.CLIENT_AGENT, clientAgentName);
		data.put(ModelStorageMap.SERVICES_ENGAGED, servicesEngaged);
		data.put(ModelStorageMap.TIME_BLOCK, dtf.print(new DateTime()));
		data.put(ModelStorageMap.START_TIME, dtf.print(startTime));
		data.put(ModelStorageMap.START_TIME_MILLIS, startTime.getMillis());
		
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
//		[data={time_block=2014-05-07 13_09_26_214, ClientAgent=833, REAL_TIME_TO_SERVE_COMPOSITION=234627, SERVICES_ENGAGED=;1113;2812, EXPECTED_TIME_TO_SERVE_COMPOSITION=20038}, eventType()=SERVICE_COMPOSITION_SUMMARY, values()={time_block=2014-05-07 13_09_26_214, ClientAgent=833, REAL_TIME_TO_SERVE_COMPOSITION=234627, EXPECTED_TIME_TO_SERVE_COMPOSITION=20038, SERVICES_ENGAGED=;1113;2812}
		return "ServiceCompositionSummary=" + data.get("time_block") + 
				"," + data.get(ModelStorageMap.EXPECTED_TIME_TO_SERVE_COMPOSITION) +
				"," + data.get(ModelStorageMap.REAL_TIME_TO_SERVE_COMPOSITION) +
				"," + data.get(ModelStorageMap.CLIENT_AGENT) +
				"," + data.get(ModelStorageMap.SERVICES_ENGAGED) +
				"," + data.get(ModelStorageMap.START_TIME) + 
				"," + data.get(ModelStorageMap.START_TIME_MILLIS); 
				
	}

	public static ServiceCompositionSummaryEvent instance(long expectedTimeToServeComposition,
			long realTimeToServeComposition, 
			String clientAgentName,
			String servicesEngaged,
			DateTime startTime) {

		return new ServiceCompositionSummaryEvent(expectedTimeToServeComposition, 
				realTimeToServeComposition, 
				clientAgentName, 
				servicesEngaged, 
				startTime);
	}

}
