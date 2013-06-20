package be.kuleuven.robustworkflows.model.messages;

/**
 * Stores data related to the completion of serving a ServiceRequest.
 * 
 * @author mario
 *
 */
public class ServiceRequestFinished {

	private final ServiceRequest serviceRequest;
	private final String factoryAgentName;

	private ServiceRequestFinished(ServiceRequest serviceRequest, String factoryAgentName) {
		this.serviceRequest = serviceRequest;
		this.factoryAgentName = factoryAgentName;
	}

	public ServiceRequest getServiceRequest() {
		return serviceRequest;
	}
	
	public String getFactoryAgentName() {
		return factoryAgentName;
	}
	
	public static ServiceRequestFinished getInstance(ServiceRequest serviceRequest, String factoryAgentName) {
		return new ServiceRequestFinished(serviceRequest, factoryAgentName);
	}

}
