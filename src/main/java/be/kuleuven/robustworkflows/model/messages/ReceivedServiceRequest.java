package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

/**
 * Keeps the information about a request and the actor who sent it
 * TODO move to another place. since this is not a message
 * @author mario
 *
 */
public class ReceivedServiceRequest implements Serializable {
	private static final long serialVersionUID = 20130821L;

	private final ServiceRequest sr;
	private final  ActorRef actor;

	private ReceivedServiceRequest(ServiceRequest sr, ActorRef actor) {
		this.sr = sr;
		this.actor = actor;
	}
	
	public static ReceivedServiceRequest getInstance(ServiceRequest sr, ActorRef actor) {
		return new ReceivedServiceRequest(sr, actor);
	}
	
	public ServiceRequest sr() {
		return sr;
	}
	
	public ActorRef actor() {
		return actor;
	}
}
