package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.UntypedActor;

/**
 * AbstractFactory responsible for abstracting which type of behaviour the ClientAgent 
 * needs to have
 * 
 * @author mario
 *
 */
public abstract class ExplorationBehaviorFactory {
	public abstract ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy);
	public abstract UntypedActor createExplorationAnt(ExplorationAntParameter parameterObject);
}
