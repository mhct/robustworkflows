package be.kuleuven.robustworkflows.model.messages;

public class ServiceRequestFinished {

	public ServiceRequestFinished(ServiceRequest serviceRequest) {
		// TODO Auto-generated constructor stub
	}

	public static ServiceRequestFinished getInstance(ServiceRequest serviceRequest) {
		return new ServiceRequestFinished(serviceRequest);
	}

}
