package be.kuleuven.robustworkflows.model.clientagent;

import java.io.Serializable;

import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.antactors.dmas.DMASExplorationAntActor;
import be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior.WaitingTaskState;

public class DMASExplorationFactory extends ExplorationBehaviorFactory implements Serializable {

	private static final long serialVersionUID = 20130923L;

	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
		return WaitingTaskState.getInstance(clientAgentProxy);
	}

	@Override
	public UntypedActor createExplorationAnt(ExplorationAntParameter antParameters) {
		return DMASExplorationAntActor.getInstance(antParameters);
	}

}
