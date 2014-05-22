package be.kuleuven.robustworkflows.model.factoryagent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;

/**
 * A ComputationalResourceProfile which process each request, expecting to execute them
 * on a average time, defined by a Normal distribution and the processingTimePerRequest. 
 * 
 * This class represents a model from a real service, that executes requests in a certain time.
 * 
 * @author mario
 *
 */
public class AverageDistributionProcessingTimeProfile extends ComputationalResourceProfile implements Serializable {

	private static final long serialVersionUID = 20130613L;

	private final long processingTimePerRequest;
	private double sigma;
	private int seed;
	private RandomDataGenerator random;
	private Queue<WorkServiceRequest> serviceRequests;


	
	public AverageDistributionProcessingTimeProfile(int processingTimePerRequest, double sigma, int seed, ServiceType serviceType) {
		super(serviceType);
		this.processingTimePerRequest = processingTimePerRequest;
		this.sigma = sigma;
		this.seed = seed;
		this.serviceRequests = new LinkedList<WorkServiceRequest>();
		this.random = new RandomDataGenerator(new MersenneTwister(seed));
	}

	@Override
	public long expectedTimeToServeRequest() {
		if (serviceRequests.size() == 0) {
			return processingTimePerRequest;
		} else {
			return processingTimePerRequest * serviceRequests.size();
		}
	}

	@Override
	public void add(ReceivedServiceRequest receivedServiceRequest) {
		long expectedTime = (long) Math.ceil(random.nextGaussian(processingTimePerRequest, sigma));
		WorkServiceRequest temp = WorkServiceRequest.getInstance(receivedServiceRequest, expectedTime);
		serviceRequests.add(temp);
	}
	
	@Override
	public ReceivedServiceRequest poll() {
		return serviceRequests.poll().req;
	}
	
	@Override
	public long blockFor() {
		return serviceRequests.peek().time;
	}
	
	@Override
	public boolean hasWork() {
		if (serviceRequests.size() > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		serviceRequests = new LinkedList<WorkServiceRequest>();
		seed++;
		random = new RandomDataGenerator(new MersenneTwister(seed));
	}

	@Override
	public int queueSize() {
		return serviceRequests.size();
	}

	static class WorkServiceRequest {
		private final ReceivedServiceRequest req;
		private final long time;

		private WorkServiceRequest(ReceivedServiceRequest req, long time) {
			this.req = req;
			this.time = time;
		}
		
		public static WorkServiceRequest getInstance(ReceivedServiceRequest req, long time) {
			return new WorkServiceRequest(req, time);
		}
	}

}
