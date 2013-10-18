package be.kuleuven.robustworkflows.model.clientagent;

import java.io.Serializable;

import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior.WaitingTaskState;

public class CompositeExplorationFactory extends ExplorationBehaviorFactory implements Serializable {

	private static final long serialVersionUID = 20130923L;

	@Override
	public ClientAgentState createWaitingState(ClientAgentProxy clientAgentProxy) {
		return WaitingTaskState.getInstance(clientAgentProxy);
	}

	/**
	 * FIXME check the parameters here
	 */
	@Override
	public UntypedActor createExplorationAnt(ExplorationAntParameter parameterObject) {
//		return CompositeExplorationAnt.getInstance(parameterObject);
		return null;
	}

}
