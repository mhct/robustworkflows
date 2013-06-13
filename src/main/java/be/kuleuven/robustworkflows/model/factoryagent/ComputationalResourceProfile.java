package be.kuleuven.robustworkflows.model.factoryagent;

import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;

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
	
	/**
	 * Returns the expected time to service a request, given x requestsPerSecond 
	 * @return
	 */
	public abstract long expectedTimeToServeRequest();

	public abstract boolean equals(Object obj);

	/**
	 * Adds a ReceivedServiceRequest
	 * 
	 * @param receivedServiceRequest
	 */
	public void add(ReceivedServiceRequest receivedServiceRequest) {
		
	}

	/**
	 * Retrieves the oldest ReceivedServiceRequest
	 * 
	 * @return the oldest ReceivedServiceRequest
	 */
	public ReceivedServiceRequest poll() {
		return null;
	}

	/**
	 * Tell the Actor how long it should "BLOCK" its operations to simulate the time for the "hardware" simulated by the ComputationProfile to 
	 * be able to perform another operation... TODO this is still not quite clear.
	 * 
	 * @return
	 */
	public long blockFor() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Creates a FixedProcessingTimeProfile
	 * 
	 * @param processingTimePerRequest
	 * @return
	 */
	public static ComputationalResourceProfile fixedProcessingTime(int processingTimePerRequest) {
		return new FixedProcessingTimeProfile(processingTimePerRequest);
	}
	
	/**
	 * Creates a ExponentialProfile
	 * 
	 * @param seed used to bootstrap the pseudo-random number generator
	 * @return
	 */
	public static ComputationalResourceProfile exponential(int seed) {
		return new ExponentialProfile(seed);
	}

	public boolean hasWork() {
		return false;
	}




}

