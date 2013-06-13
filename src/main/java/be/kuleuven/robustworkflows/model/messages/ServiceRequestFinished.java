package be.kuleuven.robustworkflows.model.messages;

public class ServiceRequestFinished {

	private final ServiceRequest serviceRequest;

	private ServiceRequestFinished(ServiceRequest serviceRequest) {
		this.serviceRequest = serviceRequest;
	}

	public ServiceRequest getServiceRequest() {
		return serviceRequest;
	}
	
	public static ServiceRequestFinished getInstance(ServiceRequest serviceRequest) {
		return new ServiceRequestFinished(serviceRequest);
	}

}
