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
	Graph<WorkflowTask> activitiesGraph; 
	
	private Workflow(Graph<WorkflowTask> workflow) {
		this.activitiesGraph = workflow;
	}
	
	/**
	 * Calculates the overall computation time for this workflow. It adds up the computation time needed to 
	 * complete each task. It is a simple SUM of all computation times from individual tasks from this workflow.
	 * 
	 * @return time to compute all workflow tasks
	 */
	public long totalComputationTime() {
		long totalTime = 0;
		for (WorkflowTask e: activitiesGraph.DFS()) {
			totalTime  += e.getQoS().getComputationTime();
		}
		
		return totalTime;
	}
	
	
	@Override
	public String toString() {
		String ret =  "Workflow [";
		for (WorkflowTask e: activitiesGraph.DFS()) {
			ret += " -> (" + e.getType() + "," + e.getAgent() + "," + e.getQoS().getComputationTime() +")";
		}
		ret += " ]\n";
		
		return ret;
	}

	@Override 
	public Iterator<WorkflowTask> iterator() {
		return activitiesGraph.DFS().iterator();
	}

	/**
	 * Returns the RAW representation of this workflow
	 * @return
	 */
	public Multimap<WorkflowTask, WorkflowTask> rawWorkflow() {
		return activitiesGraph.raw();
	}

	/**
	 * Factory Method to create a linear workflow having the types A -> B
	 * 
	 * @return a workflow with only two tasks A -> B
	 */
	public static Workflow getLinear1(){
		Graph<WorkflowTask> workflow =  Graph.create(); 
		
		workflow.put(ImmutableWorkflowTask.getInstance(ServiceType.A), ImmutableWorkflowTask.getInstance(ServiceType.B));
		
		return new Workflow(workflow);
	}

	public static Workflow getLinearInverted1(){
		Graph<WorkflowTask> workflow =  Graph.create(); 
		
		workflow.put(ImmutableWorkflowTask.getInstance(ServiceType.B), ImmutableWorkflowTask.getInstance(ServiceType.A));
		
		return new Workflow(workflow);
	}
	
	/**
	 *  Factory Method to create a workflow given the underlying map
	 */
	public static Workflow getInstance( Multimap<WorkflowTask, WorkflowTask> workflowEntries) {
		final Graph<WorkflowTask> graph = Graph.create();
		
		for (Map.Entry<WorkflowTask, WorkflowTask> e: workflowEntries.entries()) {
			graph.put(e.getKey().getImmutableWorkflowTask(), e.getValue().getImmutableWorkflowTask());
		}
		
		return new Workflow(graph);
	}


}
