package be.kuleuven.robustworkflows.infrastructure.configuration;

import akka.actor.Actor;

import com.mongodb.DB;

/**
 * Factory responsible for instantiating Model classes. That is, this factory instantiates
 * classes belonging to the emulation MODEL. Normally Agents
 *  
 * @author mario
 *
 */
public class AgentFactory {
	
	private AgentHandlerChain chain = null;

	/**
	 * Creates instances of classes given by rawType
	 * 
	 * @param rawType
	 * @param db
	 * @return
	 */
	public Actor handleInstance(Object rawType, DB db) {
		if (rawType == null || db == null) {
			throw new RuntimeException("rawType, db can not be null");
		}
		
		String agentType = (String) rawType;
		return chain.handle(agentType).createInstance(db);
	}
	
	/**
	 * Describes WHICH types of actor it should be created given a specified rawType
	 * 
	 * @param rawType
	 * @return
	 */
	public Class<? extends Actor> handle(Object rawType) {
		if (rawType == null) {
			throw new RuntimeException("rawType can not be null");
		}
		
		String agentType = (String) rawType;
		return chain.handle(agentType).factoryOf();
	}
	
	private void addHandler(AgentHandlerChain handler) {
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
}

/**
 * Chain of responsibility to handle the instantiation of MODEL classes
 * 
 * @author mario
 *
 */
abstract class AgentHandlerChain {
	
	private AgentHandlerChain next = null;
	private String handling;
	private Class<? extends Actor> factoryOf;

	AgentHandlerChain(String handling, Class<? extends Actor> factoryOf) {
		this.handling = handling;
		this.factoryOf = factoryOf;
	}

	/**
	 * Points to the next Handler of the Chain
	 * 
	 * @param next
	 */
	public void setNext(AgentHandlerChain next) {
		this.next = next;
	}
	
	/**
	 * Tries to handle a String
	 * 
	 * @param name the name of the class that should be loaded
	 * @return a class of the proper type
	 */
	public AgentHandlerChain handle(String name) {
		if (canHandle(name)) {
//			return handleIt(name);
			return this;
		} else if (next != null) {
			return next.handle(name);
		} else {
			throw new RuntimeException("Could not handle agent of type: " + name);
		}
		
	}
	
	/**
	 * Checks if the current Handler can handle a specific type, given by name
	 * Can be overrided in case the concrete handler needs to test more things.
	 * 
	 * @param name the name of the Agent type to be tested
	 * @return true if the current handler can handle such agent types
	 */
	Boolean canHandle(String name) {
		
		if (handling.equals(name)) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Handles the "name". This method can be overrided in case the concrete handler needs specific behaviour
	 *  
	 * @param name
	 * @return
	 */
//	Class<? extends Actor> handleIt(String name) {
//		
//		if (canHandle(name)) {
//			return factoryOf;
//		} else {
//			return null;
//		}
//	}
	Class<? extends Actor> factoryOf() {
		return factoryOf;
	}
	
	abstract public Actor createInstance(DB db);
	
}


