package be.kuleuven.robustworkflows.model.messages;

import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * Represents a service request from a ClientAgent
 * 
 * @author mario
 *
 */
public class ServiceRequest {
	private ServiceType serviceType;
	private final long creationTime;


	private ServiceRequest(ServiceType serviceType) {
		this.serviceType = serviceType;
		this.creationTime = System.currentTimeMillis();
	}

	public boolean typeOf(ServiceType serviceType) {
		if (this.serviceType.equals(serviceType)) {
			return true;
		} else {
			return false;
		}
	}

	public long totalTimeToServeRequest() {
		return System.currentTimeMillis() - creationTime;
	}
	
	public static ServiceRequest getInstance(ServiceType serviceType) {
		return new ServiceRequest(serviceType);
	}
}
