package be.kuleuven.robustworkflows.model.messages;

/**
 * MESSAGE Containing information about the quality of service of a particular component service 
 * @author mario
 *
 */
public class QoSData {

	private long computationTime;

	public QoSData(long computationTime) {
		this.computationTime = computationTime;
	}

	public static QoSData getInstance(long computationTime) {
		return new QoSData(computationTime);
	}

	public long getComputationTime() {
		return computationTime;
	}

	@Override
	public String toString() {
		return "QoSData [computationTime=" + computationTime + "]";
	}
	
	
}
