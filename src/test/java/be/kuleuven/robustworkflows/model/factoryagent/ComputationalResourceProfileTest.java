package be.kuleuven.robustworkflows.model.factoryagent;

import static org.junit.Assert.*;

import org.junit.Test;

import be.kuleuven.robustworkflows.model.ServiceType;

public class ComputationalResourceProfileTest {

	@Test
	public void testFixedProcessingTime() { 
		ComputationalResourceProfile profile = ComputationalResourceProfile.fixedProcessingTime(10, ServiceType.A);
		profile.reset();
		
	}

	@Test
	public void testExponential() {
		fail("Not yet implemented");
	}

}
