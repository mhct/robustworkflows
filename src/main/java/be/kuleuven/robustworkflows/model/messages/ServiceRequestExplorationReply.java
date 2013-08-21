package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;


/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class ServiceRequestExplorationReply implements Serializable {
	private static final long serialVersionUID = 20130821L;
	
	private final long computationTime; //only quality being evaluated right now
	private final ServiceRequestExploration requestExploration;

	public ServiceRequestExplorationReply(ServiceRequestExploration requestExploration, long computationTime) {
		this.requestExploration = requestExploration;
		this.computationTime = computationTime;
	}


	public long getComputationTime() {
		return computationTime;
	}
	
	public ServiceRequestExploration getRequestExploration() {
		return requestExploration;
	}

	/**
	 * Factory of ServiceRequestExplorationReply
	 * 
	 * @param requestExploration
	 * @param computationTime
	 * @return
	 */
	public static ServiceRequestExplorationReply getInstance(ServiceRequestExploration requestExploration, long computationTime) {
		return new ServiceRequestExplorationReply(requestExploration, computationTime);
	}
	
}
