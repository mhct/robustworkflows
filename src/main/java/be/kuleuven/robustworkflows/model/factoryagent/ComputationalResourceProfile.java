package be.kuleuven.robustworkflows.model.factoryagent;

/**
 * Describes a profile to reply to service requests. Profiles can be totally random, 
 * or can be modeled in any way.
 * 
 * @author mario
 *
 */
public abstract class ComputationalResourceProfile {

	protected ComputationalResourceProfile() {
	}
	
	public static ComputationalResourceProfile exponential(int seed) {
		return new ExponentialProfile(seed);
	}
	
	/**
	 * Returns the expected time to service a request, given x requestsPerSecond 
	 * @param requestsPerSecond
	 * @return
	 */
	public abstract long expectedTime(int requestsPerSecond);

	public abstract boolean equals(Object obj);
}

