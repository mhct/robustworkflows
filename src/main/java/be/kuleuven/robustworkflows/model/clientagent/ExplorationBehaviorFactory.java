package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.messages.Workflow;

/**
 * AbstractFactory responsible for abstracting which type of behaviour the ClientAgent 
 * needs to have
 * 
 * @author mario
 *
 */
public abstract class ExplorationBehaviorFactory {
	public abstract ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy);
	public abstract UntypedActor createExplorationAnt(ActorRef master, ModelStorage modelStorage, Workflow workflow, long explorationTimeout);
}
