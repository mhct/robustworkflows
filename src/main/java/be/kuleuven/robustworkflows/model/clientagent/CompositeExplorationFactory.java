package be.kuleuven.robustworkflows.model.clientagent;

import java.io.Serializable;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ant.CompositeExplorationAnt;
import be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior.WaitingTaskState;
import be.kuleuven.robustworkflows.model.messages.Workflow;

public class CompositeExplorationFactory extends ExplorationBehaviorFactory implements Serializable {

	private static final long serialVersionUID = 20130923L;

	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
		return WaitingTaskState.getInstance(clientAgentProxy);
	}

	@Override
	public UntypedActor createExplorationAnt(ActorRef master, ModelStorage modelStorage, Workflow workflow, long explorationTimeout) {
		return CompositeExplorationAnt.getInstance(master, modelStorage, workflow, explorationTimeout);
	}

}
