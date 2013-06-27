package be.kuleuven.robustworkflows.model.factoryagent;

import java.io.Serializable;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

public class ExponentialProfile extends ComputationalResourceProfile implements Serializable {
	
	private static final long serialVersionUID = 2013052301L;
	private RandomDataGenerator random;

	protected ExponentialProfile(int seed) {
		super(null);
		this.random = new RandomDataGenerator(new MersenneTwister(seed));
	}

	@Override
	public long expectedTimeToServeRequest() {
		return random.nextPoisson(10); //FIXME the mean is fixed... should be an attribute from the profile 
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
