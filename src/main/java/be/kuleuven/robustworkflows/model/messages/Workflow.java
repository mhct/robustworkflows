package be.kuleuven.robustworkflows.model.messages;

import java.util.Iterator;
import java.util.Map;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.collect.Graph;

import com.google.common.collect.Multimap;

/**
 * Represents a workflow specification
 * @Immutable
 * @author mariohct
 *
 */
public class Workflow implements Iterable<WorkflowTask> {
//	Multimap<WorkflowTask, WorkflowTask> workflow; //FIXME update to use the be.kuleuven.robustworkflows.model.collect.Graph Abstraction
	Graph<WorkflowTask> workflow; 
	
//	private Workflow(Multimap<WorkflowTask, WorkflowTask> workflow) {
//		this.workflow = workflow;
//	}

	private Workflow(Graph<WorkflowTask> workflow) {
		this.workflow = workflow;
	}
	
	/**
	 * Calculates the overall computation time for this workflow. It adds up the computation time needed to 
	 * complete each task. It is a simple SUM of all computation times from individual tasks from this workflow.
	 * 
	 * @return time to compute all workflow tasks
	 */
	public long totalComputationTime() {
		long totalTime = 0;
		for (WorkflowTask e: workflow.DFS()) {
			totalTime  += e.getQoS().getComputationTime();
		}
		
		return totalTime;
	}
	
	
	@Override
	public String toString() {
		String ret =  "Workflow [";
		for (WorkflowTask e: workflow.DFS()) {
			ret += " -> " + e.getType();
		}
		ret += " ]\n";
		
		return ret;
	}

	@Override 
	public Iterator<WorkflowTask> iterator() {
		return workflow.DFS().iterator();
	}

	/**
	 * Returns the RAW representation of this workflow
	 * @return
	 */
	public Multimap<WorkflowTask, WorkflowTask> rawWorkflow() {
		return workflow.raw();
	}

	/**
	 * Factory Method to create a linear workflow having the types A -> B
	 * 
	 * @return a workflow with only two tasks A -> B
	 */
	public static Workflow getLinear1(){
//		Multimap<WorkflowTask, WorkflowTask> workflow = LinkedListMultimap.create();
		Graph<WorkflowTask> workflow =  Graph.create(); 
		
		workflow.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.B));
		
		return new Workflow(workflow);
	}


	/**
	 * Factory Method to create linear workflows
	 * 
	 * @param types Types of services required by this workflow
	 * @return a workflow 
	 */
//	public static Workflow getLinear(ServiceType... types) {
//		Multimap<WorkflowTask, WorkflowTask> workflow = LinkedListMultimap.create();
//		for (int i=0; i<types.length-1; i++) {
//			workflow.put(WorkflowTask.getInstance(types[i]), WorkflowTask.getInstance(types[i+1]));
//		}
//		
//		return new Workflow(workflow);
//	}
	
	/**
	 *  Factory Method to create a workflow given the underlying map
	 */
	public static Workflow getInstance( Multimap<WorkflowTask, WorkflowTask> workflowEntries) {
		Graph<WorkflowTask> graph = Graph.create();
		
		for (Map.Entry<WorkflowTask, WorkflowTask> e: workflowEntries.entries()) {
			graph.put(e.getKey(), e.getValue());
		}
		
		return new Workflow(graph);
	}


}
