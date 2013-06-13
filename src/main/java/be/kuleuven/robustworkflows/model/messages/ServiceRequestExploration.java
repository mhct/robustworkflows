package be.kuleuven.robustworkflows.model.messages;

import be.kuleuven.robustworkflows.model.ServiceType;
import akka.actor.ActorRef;

public class ServiceRequestExploration {

	private ServiceType serviceType;
	private Integer numberOfHopsToTravel;
	private ActorRef origin;

	public ServiceRequestExploration(ServiceType serviceType, Integer numberOfHopsToTravel, ActorRef origin) {
		this.serviceType = serviceType;
		this.numberOfHopsToTravel = numberOfHopsToTravel;
		this.origin = origin;
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

	public static ServiceRequestExploration getInstance(ServiceType serviceType, Integer numberOfHopsToTravel, ActorRef origin) {
		return new ServiceRequestExploration(serviceType, numberOfHopsToTravel, origin);
	}

	/**
	 * Creates a new message by LOWERING the number of HOPS by 1, of a previous message
	 * 
	 * @param msg
	 * @return
	 */
	public static ServiceRequestExploration getInstanceRemovingHop(ServiceRequestExploration msg) {
		return new ServiceRequestExploration(msg.getServiceType(), msg.getNumberOfHopsToTravel() - 1, msg.getOrigin());
	}
	
}
