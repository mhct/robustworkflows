package be.kuleuven.robustworkflows.infrastructure.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.AgentAttributes;

public class DeployAgent implements Serializable {

	private static final long serialVersionUID = 201301311L;

//	private final String name;
//	private String agentType;

	private AgentAttributes attributes;
	
//	private DeployAgent(String agentType, String name) {
//		if(agentType == null || name == null || "".equals(agentType) || "".equals(name)) {
//			throw new IllegalArgumentException("AgentType and name should not be null or empty");
//		}
//		
//		this.agentType = agentType;
//		this.name = name;
//	}
	
	private DeployAgent(AgentAttributes attributes) {
		this.attributes = attributes;
	}
	
	public static DeployAgent getInstance(AgentAttributes attributes) {
		return new DeployAgent(attributes);
	}

	/**
	 * Tries to convert a message to a DeployAgent type
	 * @param message
	 * @return
	 */
	public static DeployAgent valueOf(Object message) {
		if (DeployAgent.class.isInstance(message)) {
			return (DeployAgent) message;
		}
		else {
			throw new RuntimeException("Message is not from DeplyAgent type");
		}
	}

//	public String getAgentType() {
//		return agentType;
//	}
//
//	public String getName() {
//		return name;
//	}
	
	public AgentAttributes attributes() {
		return attributes;
	}
}
