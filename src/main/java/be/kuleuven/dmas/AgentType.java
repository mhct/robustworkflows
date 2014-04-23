package be.kuleuven.dmas;

/**
 * AgentType represents the types of agents a DelegateMAS system wants to use.
 * 
 * @author mario
 *
 */
public class AgentType {
	public final String type;
	
	private AgentType(String type) {
		this.type = type;
	}

	public static AgentType factory() {
		return new AgentType("factory");
	}

	public static AgentType client() {
		return new AgentType("client");
	}
}
