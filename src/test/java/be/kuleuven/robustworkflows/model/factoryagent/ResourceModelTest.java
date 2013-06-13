package be.kuleuven.robustworkflows.model.factoryagent;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourceModelTest {

	@Test
	public void newExponential() {
		ComputationalResourceProfile rm = ComputationalResourceProfile.exponential(1);
		assertNotNull(rm);
	}

	@Test
	public void testExponentialValues() {
		ComputationalResourceProfile rm = ComputationalResourceProfile.exponential(1);
		assertEquals(2, rm.expectedTimeToServeRequest());
	}
}
