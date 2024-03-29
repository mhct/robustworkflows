package be.kuleuven.robustworkflows.model.factoryagent;

import java.io.Serializable;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class ExponentialProfile extends ComputationalResourceProfile implements Serializable {
	
	private static final long serialVersionUID = 2013052301L;
	private RandomDataGenerator random;
	private int seed;

	protected ExponentialProfile(int seed) {
		super(null);
		this.seed = seed;
		this.random = new RandomDataGenerator(new MersenneTwister(seed));
	}

	@Override
	public long expectedTimeToServeRequest() {
		//FIXME the mean is fixed... should be an attribute from the profile
		return random.nextPoisson(10);
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

	@Override
	public void reset() {
		random = new RandomDataGenerator(new MersenneTwister(seed));
	}

	@Override
	public int queueSize() {
		return 0;
	}

}
