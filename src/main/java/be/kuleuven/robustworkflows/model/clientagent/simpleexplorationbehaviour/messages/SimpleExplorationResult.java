package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.ServiceType;

import akka.actor.ActorRef;

/**
 * Represents the exploration results of a SimpleExplorationAnt
 * 
 * @author mario
 *
 */
public class SimpleExplorationResult implements Serializable {

	private static final long serialVersionUID = 20130923L;
	private final long computationTime;
	private final ActorRef actor;
	private final ServiceType serviceType;
	
	public long computationTime() {
		return computationTime;
	}
	
	public ActorRef actor() {
		return actor;
	}
	
	public ServiceType serviceType() {
		return serviceType;
	}
	
	private SimpleExplorationResult(ActorRef actor, long computationTime, ServiceType serviceType) {
		this.actor = actor;
		this.computationTime = computationTime;
		this.serviceType = serviceType;
	}
	
	public static SimpleExplorationResult getInstance(ActorRef actor, long computationTime, ServiceType serviceType) {
		return new SimpleExplorationResult(actor, computationTime, serviceType);
	}

}
