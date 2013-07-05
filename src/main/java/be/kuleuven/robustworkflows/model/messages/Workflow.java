package be.kuleuven.robustworkflows.model.messages;

import java.util.Map;

import be.kuleuven.robustworkflows.model.ServiceType;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * Represents a workflow specification
 * @Immutable
 * @author mariohct
 *
 */
public class Workflow {
	Multimap<WorkflowTask, WorkflowTask> workflow; //FIXME update to use AdjacencyList abstraction, or even better, Graph Abstraction
	
	private Workflow(Multimap<WorkflowTask, WorkflowTask> workflow) {
		this.workflow = workflow;
	}
	
	/**
	 * Calculates the overall computation time for this workflow. It adds up the computation time needed to 
	 * complete each task. It is a simple SUM of all computation times from individual tasks from this workflow.
	 * 
	 * @return time to compute all workflow tasks
	 */
	public long totalComputationTime() {
		WorkflowTask head = workflow.keySet().iterator().next();
		long totalTime = 0;
		
		if (head != null) {
			totalTime = head.getQoS().getComputationTime();
		}
		
		for (Map.Entry<WorkflowTask, WorkflowTask> e: workflow.entries()) {
			totalTime += e.getValue().getQoS().getComputationTime();
		}
		
		return totalTime;
	}
	
	/**
	 * Factory Method to create a linear workflow having the types A -> B
	 * 
	 * @return a workflow with only two tasks A -> B
	 */
	public static Workflow getLinear1(){
		Multimap<WorkflowTask, WorkflowTask> workflow = LinkedListMultimap.create();
		workflow.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.B));
		
		return new Workflow(workflow);
	}

	/**
	 * Returns the RAW representation of this workflow
	 * @return
	 */
	public Multimap<WorkflowTask, WorkflowTask> rawWorkflow() {
		return LinkedListMultimap.create(workflow);
	}

	/**
	 * Factory Method to create linear workflows
	 * 
	 * @param types Types of services required by this workflow
	 * @return a workflow 
	 */
	public static Workflow getLinear(ServiceType... types) {
		Multimap<WorkflowTask, WorkflowTask> workflow = LinkedListMultimap.create();
		for (int i=0; i<types.length-1; i++) {
			workflow.put(WorkflowTask.getInstance(types[i]), WorkflowTask.getInstance(types[i+1]));
		}
		
		return new Workflow(workflow);
	}
	
	/**
	 *  Factory Method to create a workflow given the underlying map
	 */
	public static Workflow getInstance( Multimap<WorkflowTask, WorkflowTask> workflow) {
		return new Workflow(LinkedListMultimap.create(workflow));
	}
}
