package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.Workflow;

public class ExplorationAntParameter {
	private ActorRef master;
	private Workflow workflow;
	private long explorationTimeout;
	private double samplingProbability;

	public ExplorationAntParameter(ActorRef master, Workflow workflow, long explorationTimeout, double samplingProbability) {
		this.master = master;
		this.workflow = workflow;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
	}

	public ActorRef getMaster() {
		return master;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public long getExplorationTimeout() {
		return explorationTimeout;
	}

	public double getSamplingProbability() {
		return samplingProbability;
	}
}