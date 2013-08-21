package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * Represents a service request from a ClientAgent
 * 
 * @author mario
 *
 */
public class ServiceRequest implements Serializable {
	private static final long serialVersionUID = 20130821L;
	
	private ServiceType serviceType;
	private final long creationTime;
	private int id;


	private ServiceRequest(int id, ServiceType serviceType) {
		this.id = id;
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
	
	public static ServiceRequest getInstance(int id, ServiceType serviceType) {
		return new ServiceRequest(id, serviceType);
	}

	public int getId() {
		return id;
	}
}
