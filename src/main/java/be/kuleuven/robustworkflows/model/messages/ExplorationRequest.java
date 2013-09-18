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
	
	
}
