package be.kuleuven.robustworkflows.infrastructure.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

public class ActorDeployRef implements Serializable{

	private static final long serialVersionUID = 2013020601L;
	private final ActorRef ref;
	private final String nodeName;

	public ActorDeployRef(ActorRef ref, String nodeName) {
		this.ref = ref;
		this.nodeName = nodeName;
	}

	public ActorRef getRef() {
		return ref;
	}

	public String getNodeName() {
		return nodeName;
	}

	
}
