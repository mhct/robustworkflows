package be.kuleuven.robustworkflows.infrastructure.configuration;

import java.util.ArrayList;

import akka.actor.Actor;
import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.FactoryAgent;

import com.mongodb.DB;

public class FactoryAgentHandler extends AgentHandlerChain {
	
	public FactoryAgentHandler() {
		super("Factory", FactoryAgent.class);
	}

	@Override
	public Actor createInstance(DB db) {
		return new FactoryAgent(db, new ArrayList<ActorRef>());
	}
}
