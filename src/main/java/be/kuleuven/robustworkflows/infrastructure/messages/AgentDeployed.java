package be.kuleuven.robustworkflows.infrastructure.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

public class AgentDeployed implements Serializable{

	private static final long serialVersionUID = 2013020601L;
	private final ActorRef ref;
	private String nodeName;
	private String agentTpye;

	public AgentDeployed(ActorRef ref, String nodeName, String agentType) {
		this.ref = ref;
		this.nodeName = nodeName;
		this.agentTpye = agentType;
	}

	public ActorRef getRef() {
		return ref;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getAgentType() {
		return agentTpye;
	}
}
