package be.kuleuven.robustworkflows.model.ant.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.ServiceType;

public class ExploreService implements Serializable {

	private static final long serialVersionUID = 3827680075366174715L;
	private ServiceType serviceType;

	public ServiceType getServiceType() {
		return serviceType;
	}
	
	private ExploreService(ServiceType serviceType) {
		this.serviceType = serviceType;
	}
	
	public static ExploreService type(ServiceType serviceType) {
		return new ExploreService(serviceType);
	}

}
