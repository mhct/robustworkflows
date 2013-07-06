package be.kuleuven.robustworkflows.model.messages;


/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class ExplorationReply {

	private final long computationTime; //only quality being evaluated right now
	private final ExplorationRequest requestExploration;

	public ExplorationReply(ExplorationRequest requestExploration, long computationTime) {
		this.requestExploration = requestExploration;
		this.computationTime = computationTime;
	}


	public long getComputationTime() {
		return computationTime;
	}
	
	public ExplorationRequest getRequestExploration() {
		return requestExploration;
	}

	/**
	 * Factory of ServiceRequestExplorationReply
	 * 
	 * @param requestExploration
	 * @param computationTime
	 * @return
	 */
	public static ExplorationReply getInstance(ExplorationRequest requestExploration, long computationTime) {
		return new ExplorationReply(requestExploration, computationTime);
	}
	
}
