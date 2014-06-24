package be.kuleuven.robustworkflows.model.clientagent;

import java.io.Serializable;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.antactors.dmas2.DMAS2ExplorationAntActor;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.SimpleWaitingTaskState;


//FIXME this class is loading DMAS2 solutions and not DMAS solutions
public class DMASExplorationFactory extends ExplorationBehaviorFactory implements Serializable {
	
	private static final long serialVersionUID = 20130923L;
	
	public DMASExplorationFactory() {
		System.out.println("DMASExplorationFactory Loaded");
	}

	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
//		return WaitingTaskState.getInstance(clientAgentProxy);
		return SimpleWaitingTaskState.getInstance(clientAgentProxy);
	}

	@Override
	public UntypedActor createExplorationAnt(ExplorationAntParameter antParameters) {
		return DMAS2ExplorationAntActor.getInstance(antParameters);
	}

}
