package be.kuleuven.robustworkflows.model.messages;



/**
 * Holds the exploration data obtained by an ExplorationAnt
 * 
 * @author mario
 *
 */
public class ExplorationResult {
	private Workflow workflow;
	
	public ExplorationResult(Workflow workflow) {
		this.workflow = workflow;
	}
	
	/**
	 * Calculates the total computation time, given by the collected QoSData
	 * 
	 * @return total computation time (in milliseconds) for the selected FactoryAgents (ActorRef)
	 */
	public long totalComputationTime() {
		return workflow.totalComputationTime();
	}


	@Override
	public String toString() {
		return "ExplorationResult [workflow=" + workflow + "]";
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public static ExplorationResult getInstance(final Workflow workflow) {
		return new ExplorationResult(workflow);
	}
	
}
