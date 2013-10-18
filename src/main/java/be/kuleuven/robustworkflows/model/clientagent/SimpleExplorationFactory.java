package be.kuleuven.robustworkflows.model.clientagent;

import java.io.Serializable;

import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ant.SimpleExplorationAnt;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.SimpleWaitingTaskState;

public class SimpleExplorationFactory extends
		ExplorationBehaviorFactory implements Serializable {

	private static final long serialVersionUID = 24092013L;

	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
		return SimpleWaitingTaskState.getInstance(clientAgentProxy);
	}

	@Override
	public UntypedActor createExplorationAnt(ExplorationAntParameter parameterObject) {
		return SimpleExplorationAnt.getInstance(parameterObject);
	}

}
