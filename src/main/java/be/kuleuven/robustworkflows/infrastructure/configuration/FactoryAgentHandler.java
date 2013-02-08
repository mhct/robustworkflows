package be.kuleuven.robustworkflows.infrastructure.configuration;

import be.kuleuven.robustworkflows.model.FactoryAgent;

public class FactoryAgentHandler extends AgentHandlerChain {
	
	public FactoryAgentHandler() {
		super("Factory", FactoryAgent.class);
	}
}
