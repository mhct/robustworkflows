package be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior.messages;

import java.io.Serializable;

import be.kuleuven.robustworkflows.model.messages.Workflow;

/**
 * Represents the exploration results of a SimpleExplorationAnt
 * 
 * @author mario
 *
 */
public class DMASExplorationResult implements Serializable {

	private static final long serialVersionUID = 20130923L;
	private final Workflow workflow;
	
	public DMASExplorationResult(Workflow workflow) {
		this.workflow = workflow;
	}

	public Workflow getWorkflow() {
		return workflow;
	}
	
	public boolean isComplete() {
		return workflow.fulfilled();
	}

	public static DMASExplorationResult getInstance(Workflow workflow) {
		return new DMASExplorationResult(workflow.getImmutableClone());
	}

}
