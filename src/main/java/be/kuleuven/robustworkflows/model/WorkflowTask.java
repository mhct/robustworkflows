package be.kuleuven.robustworkflows.model;

/**
 * A task in a workflow.
 * 
 * @author mario
 *
 */
public class WorkflowTask {
	private ServiceType type;

	private WorkflowTask(ServiceType type) {
		this.type = type;
	}

	public ServiceType getType() {
		return type;
	}
	
	public static WorkflowTask getInstance(ServiceType type) {
		return new WorkflowTask(type);
	}
}
