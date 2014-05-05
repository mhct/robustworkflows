package be.kuleuven.robustworkflows.infrastructure.configuration;

import akka.actor.Actor;
import be.kuleuven.robustworkflows.model.AgentAttributes;

/**
 * Chain of responsibility to handle the instantiation of MODEL classes
 * TODO change the name of this class
 * 
 * @author mario
 *
 */
public abstract class AgentHandlerChain {
	
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
	public AgentHandlerChain handle(AgentAttributes attributes) {
		if (canHandle(attributes.getAgentType())) {
//			return handleIt(name);
			return this;
		} else if (next != null) {
			return next.handle(attributes);
		} else {
			throw new RuntimeException("Could not handle agent of type: " + attributes.getAgentType());
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
	
	abstract public Actor createInstance(AgentAttributes attributes);
	
}
