package be.kuleuven.robustworkflows.model.factoryagent;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;

public class FixedProcessingTimeProfileTest {

	@Test
	public void testBlockFor() {
		int blockFor = 10;
		ComputationalResourceProfile profile = FixedProcessingTimeProfile.fixedProcessingTime(blockFor, ServiceType.A);
		assertEquals(blockFor, profile.blockFor());
	}

	@Test
	public void addRequestsTestExpectedTimeToServe() {
		int blockFor = 67800;
		ComputationalResourceProfile profile = FixedProcessingTimeProfile.fixedProcessingTime(blockFor, ServiceType.A);
		
		assertEquals(blockFor, profile.expectedTimeToServeRequest());
		profile.add(Mockito.mock(ReceivedServiceRequest.class));
		assertEquals(blockFor*2, profile.expectedTimeToServeRequest());

		profile.add(Mockito.mock(ReceivedServiceRequest.class));
		assertEquals(blockFor*3, profile.expectedTimeToServeRequest());
		
		profile.poll();
		assertEquals(blockFor*2, profile.expectedTimeToServeRequest());

		profile.poll();
		assertEquals(blockFor*1, profile.expectedTimeToServeRequest());

		profile.poll();
		assertEquals(blockFor*1, profile.expectedTimeToServeRequest());
	}
	
	@Test
	public void testDoesNOTHaveWork() {
		int blockFor = 67800;
		ComputationalResourceProfile profile = FixedProcessingTimeProfile.fixedProcessingTime(blockFor, ServiceType.A);

		assertFalse(profile.hasWork());
	}
	
	@Test
	public void testHasWork() {
		int blockFor = 67800;
		ComputationalResourceProfile profile = FixedProcessingTimeProfile.fixedProcessingTime(blockFor, ServiceType.A);
		
		profile.add(Mockito.mock(ReceivedServiceRequest.class));
		assertTrue(profile.hasWork());
	}
}
