package be.kuleuven.robustworkflows.infrastructure.configuration;

import akka.actor.Actor;
import be.kuleuven.robustworkflows.model.AgentAttributes;

import com.mongodb.DB;

/**
 * Factory responsible for instantiating Model classes. That is, this factory instantiates
 * classes belonging to the emulation MODEL. Normally Agents
 *  
 * @author mario
 *
 */
public class AgentFactory {
	
	private static AgentHandlerChain chain = null;

	/**
	 * Creates instances of classes given by rawType
	 * 
	 * @param attributes
	 * @param db
	 * @return
	 */
	public Actor handleInstance(AgentAttributes attributes) {
		if (attributes == null) {
			throw new RuntimeException("rawType can not be null");
		}
		
		return chain.handle(attributes).createInstance(attributes);
	}

	public static Actor handleInstanceCreation(AgentAttributes attributes) {
		if (attributes == null) {
			throw new RuntimeException("rawType can not be null");
		}
		
		return chain.handle(attributes).createInstance(attributes);
	}
	
	/**
	 * Describes WHICH types of actor it should be created given a specified rawType
	 * 
	 * @param attributes
	 * @return
	 */
	public Class<? extends Actor> handle(AgentAttributes attributes) {
		if (attributes == null) {
			throw new RuntimeException("rawType can not be null");
		}
		
		return chain.handle(attributes).factoryOf();
	}
	
	private static void addHandler(AgentHandlerChain handler) {
		if (chain == null) {
			chain = handler;
		} else {
			handler.setNext(chain);
			chain = handler;
		}
	}
	
	public static AgentFactory getInstance() {
		AgentFactory af = new AgentFactory();
		af.addHandler(new FactoryAgentHandler());
		af.addHandler(new ClientAgentHandler());
		
		return af;
	}

	public static void initialize() {
		AgentFactory.addHandler(new FactoryAgentHandler());
		AgentFactory.addHandler(new ClientAgentHandler());
	}
}



