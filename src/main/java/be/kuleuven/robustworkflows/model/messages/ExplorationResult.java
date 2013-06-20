package be.kuleuven.robustworkflows.model.messages;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import akka.actor.ActorRef;

/**
 * Holds the exploration data obtained by an ExplorationAnt
 * 
 * @author mario
 *
 */
public class ExplorationResult {

	private final Map<ActorRef, QoSData> data;
	
	public ExplorationResult(Map<ActorRef, QoSData> data) {
		this.data = data;
	}
	
	/**
	 * Calculates the total computation time, given by the collected QoSData
	 * 
	 * @return total computation time (in milliseconds) for the selected FactoryAgents (ActorRef)
	 */
	public long totalComputationTime() {
		long totalComputationTime = 0;
		for (Map.Entry<ActorRef, QoSData> e: data.entrySet()) {
			totalComputationTime += e.getValue().getComputationTime();
		}
		
		return totalComputationTime;
	}

	public Set<ActorRef> getFactoryAgents() {
		return new HashSet<ActorRef>(data.keySet());
	}

	public static ExplorationResult getInstance(Map<ActorRef, QoSData> data) {
		return new ExplorationResult(Maps.newHashMap(data));
	}
}
