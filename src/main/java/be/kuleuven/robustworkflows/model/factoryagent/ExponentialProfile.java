package be.kuleuven.robustworkflows.model.factoryagent;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class ExponentialProfile extends ComputationalResourceProfile {
	
	private RandomDataGenerator random;

	protected ExponentialProfile(int seed) {
		super();
		this.random = new RandomDataGenerator(new MersenneTwister(seed));
	}

	@Override
	public long expectedTime(int requestsPerSecond) {
		return random.nextPoisson(Math.exp(requestsPerSecond)); 
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExponentialProfile other = (ExponentialProfile) obj;
		if (random == null) {
			if (other.random != null)
				return false;
		} else if (!random.equals(other.random))
			return false;
		return true;
	}
	
	
}
