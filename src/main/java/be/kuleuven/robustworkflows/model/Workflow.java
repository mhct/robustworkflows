package be.kuleuven.robustworkflows.model;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a workflow specification
 * @author mariohct
 *
 */
public class Workflow {
	Multimap<WorkflowTask, WorkflowTask> workflow;
	
	private Workflow(Multimap<WorkflowTask, WorkflowTask> workflow) {
		this.workflow = workflow;
	}
	
	public static Workflow getLinear1(){
		Multimap<WorkflowTask, WorkflowTask> workflow = ArrayListMultimap.create();
		workflow.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.B));
		
		return new Workflow(workflow);
	}

	/**
	 * Returns the service type required by the workflow.. at a given position
	 * @param i
	 * @return
	 */
	public ServiceType get(int i) {
		return workflow.keys().toArray(new ServiceType[workflow.size()])[i]; //TODO check how to do this in a better way.. now has to convert entire workflow
		// in order to get a single element
	}
}
