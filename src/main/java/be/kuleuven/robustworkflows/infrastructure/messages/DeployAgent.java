package be.kuleuven.robustworkflows.infrastructure.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.AgentAttributes;

/**
 * Message used to request the deploy of an Agent 
 * @author mario
 *
 */
public class DeployAgent implements Serializable {

	private static final long serialVersionUID = 201301311L;

	private final AgentAttributes attributes;
	
	private DeployAgent(AgentAttributes attributes) {
		this.attributes = attributes;
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

	public AgentAttributes attributes() {
		return attributes;
	}
	
	public static DeployAgent getInstance(AgentAttributes attributes) {
		return new DeployAgent(attributes);
	}
}
