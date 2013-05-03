package be.kuleuven.robustworkflows.model.messages;

/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class QoSData {

	private long computationTime;
	private String serviceType;

	public QoSData(String serviceType, long computationTime) {
		this.serviceType = serviceType;
		this.computationTime = computationTime;
	}

	public static QoSData getInstance(String serviceType, long computationTime) {
		return new QoSData(serviceType, computationTime);
	}

	public long getComputationTime() {
		return computationTime;
	}
	
	public String getServiceType() {
		return serviceType;
	}

	@Override
	public String toString() {
		return "QoSData [computationTime=" + computationTime + ", serviceType="
				+ serviceType + "]";
	}

	
}
