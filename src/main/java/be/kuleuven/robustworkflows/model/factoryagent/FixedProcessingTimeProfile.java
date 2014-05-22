package be.kuleuven.robustworkflows.model.factoryagent;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Queue;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;

/**
 * This class represents an ComputationalResourceProfile which process each request using a fixed 
 * processing time per request.
 * 
 * 
 * @author mario
 *
 */
public class FixedProcessingTimeProfile extends ComputationalResourceProfile implements Serializable {

	private static final long serialVersionUID = 20130613L;

	private final long processingTimePerRequest;
	private Queue<ReceivedServiceRequest> serviceRequests;
	
	public FixedProcessingTimeProfile(int processingTimePerRequest, ServiceType serviceType) {
		super(serviceType);
		//Add probability to the processing time per request
		this.processingTimePerRequest = processingTimePerRequest;
		this.serviceRequests = new LinkedList<ReceivedServiceRequest>();
	}

	@Override
	public long expectedTimeToServeRequest() {
		return processingTimePerRequest + processingTimePerRequest * serviceRequests.size();
	}

	@Override
	public void add(ReceivedServiceRequest receivedServiceRequest) {
		serviceRequests.add(receivedServiceRequest);
	}
	
	@Override
	public ReceivedServiceRequest poll() {
		return serviceRequests.poll();
	}
	
	@Override
	public long blockFor() {
		return (long) processingTimePerRequest;
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
		serviceRequests = new LinkedList<ReceivedServiceRequest>();		
	}

	@Override
	public int queueSize() {
		return serviceRequests.size();
	}

}
