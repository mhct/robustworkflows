package be.kuleuven.robustworkflows.infrastructure.configuration;

import java.util.ArrayList;

import akka.actor.Actor;
import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.factoryagent.ExponentialProfile;
import be.kuleuven.robustworkflows.model.factoryagent.FactoryAgent;

import com.mongodb.DB;

public class FactoryAgentHandler extends AgentHandlerChain {
	
	public FactoryAgentHandler() {
		super("Factory", FactoryAgent.class);
	}

	@Override
	public Actor createInstance(AgentAttributes attributes, DB db) {
		return new FactoryAgent(db, new ArrayList<ActorRef>(), attributes.getComputationalProfile());
	}
}
