package be.kuleuven.robustworkflows.model.messages;

import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class ServiceRequestExplorationReply {

	private long computationTime; //only quality being evaluated right now
	private ServiceType serviceType;

	public ServiceRequestExplorationReply(ServiceType serviceType, long computationTime) {
		this.serviceType = serviceType;
		this.computationTime = computationTime;
	}

	public static ServiceRequestExplorationReply getInstance(ServiceType serviceType, long computationTime) {
		return new ServiceRequestExplorationReply(serviceType, computationTime);
	}

	public long getComputationTime() {
		return computationTime;
	}
	
	public ServiceType getServiceType() {
		return serviceType;
	}

	@Override
	public String toString() {
		return "QoSData [computationTime=" + computationTime + ", serviceType="
				+ serviceType + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (computationTime ^ (computationTime >>> 32));
		result = prime * result
				+ ((serviceType == null) ? 0 : serviceType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceRequestExplorationReply other = (ServiceRequestExplorationReply) obj;
		if (computationTime != other.computationTime)
			return false;
		if (serviceType != other.serviceType)
			return false;
		return true;
	}

	
	
}
