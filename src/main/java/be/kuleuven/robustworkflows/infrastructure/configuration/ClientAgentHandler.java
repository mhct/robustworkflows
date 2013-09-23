package be.kuleuven.robustworkflows.infrastructure.configuration;

import java.util.ArrayList;

import akka.actor.Actor;
import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgent;

import com.mongodb.DB;

public class ClientAgentHandler extends AgentHandlerChain {
	
	public ClientAgentHandler() {
		super("Client", ClientAgent.class);
	}

	@Override
	public Actor createInstance(AgentAttributes attributes, DB db) {
		return new ClientAgent(db, new ArrayList<ActorRef>(), attributes, attributes.getBehaviorFactory());
	}
}
