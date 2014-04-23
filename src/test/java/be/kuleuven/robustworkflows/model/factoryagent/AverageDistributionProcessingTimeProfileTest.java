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
		final int seed = 34770;
		final double sigma = 0.7;
		int avg = 10;
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(avg, sigma, seed, ServiceType.A);
		long res1 = profile.expectedTimeToServeRequest();
		System.out.println("res1: " + res1);
		assertEquals(avg, res1);
	}
	
	@Test
	public void testExpectedTimeToServeRequestHavingQueuedRequests() {
		final int seed = 34770;
		final double sigma = 0.7;
		final int avg = 10;
		ComputationalResourceProfile profile = ComputationalResourceProfile.average(avg, sigma, seed, ServiceType.A);
		profile.add(mock(ReceivedServiceRequest.class));
		profile.add(mock(ReceivedServiceRequest.class));
		profile.add(mock(ReceivedServiceRequest.class));
		profile.add(mock(ReceivedServiceRequest.class));
		long res1 = profile.expectedTimeToServeRequest();
		assertEquals(4*avg, res1);
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
