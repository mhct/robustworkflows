package be.kuleuven.robustworkflows.model.messages;

import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class QoSData {

	private long computationTime; //only quality being evaluated right now
	private ServiceType serviceType;

	public QoSData(ServiceType serviceType, long computationTime) {
		this.serviceType = serviceType;
		this.computationTime = computationTime;
	}

	public static QoSData getInstance(ServiceType serviceType, long computationTime) {
		return new QoSData(serviceType, computationTime);
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

	
}
