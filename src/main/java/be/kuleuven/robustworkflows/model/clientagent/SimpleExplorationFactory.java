package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ant.SimpleExplorationAnt;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.WaitingTaskState;
import be.kuleuven.robustworkflows.model.messages.Workflow;

public class SimpleExplorationFactory extends
		ExplorationBehaviorFactory {

	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
		return WaitingTaskState.getInstance(clientAgentProxy);
	}

	@Override
	public UntypedActor createExplorationAnt(ActorRef master,
			ModelStorage modelStorage, Workflow workflow,
			long explorationTimeout) {
		return SimpleExplorationAnt.getInstance(master, modelStorage, null, explorationTimeout);
	}

}
