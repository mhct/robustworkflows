package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.ServiceType;
import akka.actor.ActorRef;

/**
 * This class represents a message to query agents about what-if they receive a service request of service type X.
 * 
 * @author mario
 *
 */
public class ExplorationRequest implements Serializable {

	private static final long serialVersionUID = 20130821L;

	public static String eventType = "ExplorationRequest";
	
	private final ServiceType serviceType;
	private final Integer numberOfHopsToTravel;
	private final ActorRef origin;
	private final long id;

	private String clientAgentName;

	public ExplorationRequest(long id, ServiceType serviceType, Integer numberOfHopsToTravel, ActorRef origin, String clientAgentName) {
		this.id = id;
		this.serviceType = serviceType;
		this.numberOfHopsToTravel = numberOfHopsToTravel;
		this.origin = origin;
		this.clientAgentName = clientAgentName;
	}
	

	public ServiceType getServiceType() {
		return serviceType;
	}

	public Integer getNumberOfHopsToTravel() {
		return numberOfHopsToTravel;
	}

	public ActorRef getOrigin() {
		return origin;
	}

	public String getOriginName() {
		return clientAgentName;
	}
	
	public long getId() {
		return id;
	}
	
	public static ExplorationRequest getInstance(long id, ServiceType serviceType, Integer numberOfHopsToTravel, ActorRef origin, String clientAgentName) {
		return new ExplorationRequest(id, serviceType, numberOfHopsToTravel, origin, clientAgentName);
	}

	/**
	 * Creates a new message by LOWERING the number of HOPS by 1, of a previous message
	 * 
	 * @param msg
	 * @return
	 */
	public static ExplorationRequest getInstanceRemovingHop(ExplorationRequest msg) {
		return new ExplorationRequest(msg.getId(), msg.getServiceType(), msg.getNumberOfHopsToTravel() - 1, msg.getOrigin(), msg.getOriginName());
	}


	@Override
	public String toString() {
		return "ExplorationRequest [serviceType=" + serviceType
				+ ", numberOfHopsToTravel=" + numberOfHopsToTravel
				+ ", origin=" + origin + ", id=" + id + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientAgentName == null) ? 0 : clientAgentName.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime
				* result
				+ ((numberOfHopsToTravel == null) ? 0 : numberOfHopsToTravel
						.hashCode());
		result = prime * result + ((origin == null) ? 0 : origin.hashCode());
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
		ExplorationRequest other = (ExplorationRequest) obj;
		if (clientAgentName == null) {
			if (other.clientAgentName != null)
				return false;
		} else if (!clientAgentName.equals(other.clientAgentName))
			return false;
		if (id != other.id)
			return false;
		if (numberOfHopsToTravel == null) {
			if (other.numberOfHopsToTravel != null)
				return false;
		} else if (!numberOfHopsToTravel.equals(other.numberOfHopsToTravel))
			return false;
		if (origin == null) {
			if (other.origin != null)
				return false;
		} else if (!origin.equals(other.origin))
			return false;
		if (serviceType != other.serviceType)
			return false;
		return true;
	}
	
	
}
