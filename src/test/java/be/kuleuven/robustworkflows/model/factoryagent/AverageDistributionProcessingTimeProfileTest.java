package be.kuleuven.robustworkflows.model.factoryagent;

import static org.junit.Assert.*;


import org.junit.Test;
import org.mockito.Mockito;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;
import static org.mockito.Mockito.mock;

public class AverageDistributionProcessingTimeProfileTest {

	@Test
	public void testFixedProcessingTime() { 
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(10, 2, 10, ServiceType.A);
		assertNotNull(profile);
//		profile.reset();
	}

	@Test
	public void testExpectedTimeToServeRequest() {
		int expected = 10;
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(expected, 0.7, 10, ServiceType.A);
		long res1 = profile.expectedTimeToServeRequest();
		assertEquals(expected, res1);
	}
	
	@Test
	public void testExpectedTimeToServeRequestHavingQueuedRequests() {
		int expected = 10;
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(expected, 0.7, 10, ServiceType.A);
		profile.add(mock(ReceivedServiceRequest.class));
		profile.add(mock(ReceivedServiceRequest.class));
		profile.add(mock(ReceivedServiceRequest.class));
		profile.add(mock(ReceivedServiceRequest.class));
		long res1 = profile.expectedTimeToServeRequest();
		assertEquals(4*expected, res1);
	}
	
	
	
	@Test
	public void testReset() {
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(10, 0.7, 10, ServiceType.A);
		profile.add(Mockito.mock(ReceivedServiceRequest.class));
		long res1 = profile.expectedTimeToServeRequest();
		System.out.println(res1);
		profile.reset();
		long res2 = profile.expectedTimeToServeRequest();
		System.out.println(res2);
		assertNotSame(res1, res2);
	}

	@Test
	public void testBlockFor() {
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(10, 0.7, 10, ServiceType.A);
		profile.add(Mockito.mock(ReceivedServiceRequest.class));
		profile.add(Mockito.mock(ReceivedServiceRequest.class));
		long res1 = profile.blockFor();
		profile.poll();
		System.out.println("t" + res1);
		res1 = profile.blockFor();
		System.out.println("t" + res1);
	}
}
