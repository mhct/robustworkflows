package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages;

import java.io.Serializable;

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

	public long computationTime() {
		return computationTime;
	}
	
	public ActorRef actor() {
		return actor;
	}
	
	private SimpleExplorationResult(ActorRef actor, long computationTime) {
		this.actor = actor;
		this.computationTime = computationTime;
	}
	
	public static SimpleExplorationResult getInstance(ActorRef actor, long computationTime) {
		return new SimpleExplorationResult(actor, computationTime);
	}

}
