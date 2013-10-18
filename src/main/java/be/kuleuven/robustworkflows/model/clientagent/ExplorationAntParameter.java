package be.kuleuven.robustworkflows.model.clientagent;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.messages.Workflow;

public class ExplorationAntParameter {
	private ActorRef master;
	private ModelStorage modelStorage;
	private Workflow workflow;
	private long explorationTimeout;
	private double samplingProbability;

	public ExplorationAntParameter(ActorRef master, ModelStorage modelStorage,
			Workflow workflow, long explorationTimeout,
			double samplingProbability) {
		this.master = master;
		this.modelStorage = modelStorage;
		this.workflow = workflow;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
	}

	public ActorRef getMaster() {
		return master;
	}

	public ModelStorage getModelStorage() {
		return modelStorage;
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