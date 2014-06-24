package be.kuleuven.robustworkflows.model.clientagent;

import java.io.Serializable;

import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.antactors.reactive.ReactiveExplorationAntActor;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.SimpleWaitingTaskState;

public class ReactiveExplorationFactory extends
		ExplorationBehaviorFactory implements Serializable {

	private static final long serialVersionUID = 24092013L;

	public ReactiveExplorationFactory() {
		System.out.println("ReactiveExplorationFactory Loaded");
	}
	
	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
		return SimpleWaitingTaskState.getInstance(clientAgentProxy);
	}

	@Override
	public UntypedActor createExplorationAnt(ExplorationAntParameter parameterObject) {
		return ReactiveExplorationAntActor.getInstance(parameterObject);
	}

}
