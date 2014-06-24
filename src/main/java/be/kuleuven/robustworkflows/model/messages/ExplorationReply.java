package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;


/**
 * MESSAGE Containing information about the quality of service of a particular component service
 * A reply that can not be execute is represented by an ExplorationReply with computationTime = Long.MAX_VALUE
 *  
 * @author mario
 *
 */
public class ExplorationReply implements Serializable {

	private static final long serialVersionUID = 20130821L;
	
	private final long computationTime; //only quality being evaluated right now
	private final ExplorationRequest requestExploration;

	private double pheroLevel;

	public ExplorationReply(ExplorationRequest requestExploration, long computationTime, double pheroLevel) {
		this.requestExploration = requestExploration;
		this.computationTime = computationTime;
		this.pheroLevel = pheroLevel;
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
		return new ExplorationReply(requestExploration, computationTime, 0.001);
	}

	public static ExplorationReply getInstance(ExplorationRequest requestExploration, long computationTime, double pheroLevel) {
		return new ExplorationReply(requestExploration, computationTime, pheroLevel);
	}


	public double getPheroLevel() {
		return pheroLevel;
	}


	@Override
	public String toString() {
		return "ExplorationReply [computationTime=" + computationTime
				+ ", pheroLevel=" + pheroLevel + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (computationTime ^ (computationTime >>> 32));
		result = prime
				* result
				+ ((requestExploration == null) ? 0 : requestExploration
						.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExplorationReply other = (ExplorationReply) obj;
		if (computationTime != other.computationTime)
			return false;
		if (requestExploration == null) {
			if (other.requestExploration != null)
				return false;
		} else if (!requestExploration.equals(other.requestExploration))
			return false;
		return true;
	}


	public boolean isPossible() {
		if (computationTime == Long.MAX_VALUE) {
			return false;
		} else {
			return true;
		}
	}
	
	public static ExplorationReply notPossible(ExplorationRequest msg) {
		return new ExplorationReply(msg, Long.MAX_VALUE, 0.001);
	}
	
	
}
