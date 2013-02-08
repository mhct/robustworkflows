package be.kuleuven.robustworkflows.infrastructure.configuration;

import be.kuleuven.robustworkflows.model.ClientAgent;

public class ClientAgentHandler extends AgentHandlerChain {
	
	public ClientAgentHandler() {
		super("Client", ClientAgent.class);
	}
}
