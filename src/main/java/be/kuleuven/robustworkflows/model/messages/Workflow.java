package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;
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
public class Workflow implements Iterable<WorkflowTask>, Serializable {
	private static final long serialVersionUID = 20130821L;
	
	Graph<WorkflowTask> activitiesGraph; 
	
	private Workflow(Graph<WorkflowTask> workflow) {
		this.activitiesGraph = workflow;
	}
	
	/**
	 * Calculates the overall computation time for this workflow. It adds up the computation time needed to 
	 * complete each task. It is a simple SUM of all computation times from individual tasks from this workflow.
	 *
	 * If the workflow is fulfilled, that is, has agents assigned to each workflow activity, then, 
	 * calculates the expected time to perform the workflow, otherwise it simply returns a maximum value for long.
	 * 
	 * @return time to compute all workflow tasks
	 */
	public long totalComputationTime() {
		long totalTime = Long.MAX_VALUE;
		
		if (fulfilled()) {
			totalTime = 0;

			for (WorkflowTask w: activitiesGraph.DFS()) {
				totalTime  += w.getQoS().getComputationTime();
			}
		}
		
		return totalTime;
	}
	
	public Boolean fulfilled() {
		boolean complete = true;
		for (WorkflowTask w: activitiesGraph.DFS()) {
			if (w.getAgent() == null || w.getQoS() == null) {
				complete = false;
			}
		}

		return complete;
	}
	
	@Override
	public String toString() {
		String ret =  "Workflow [";
		for (WorkflowTask e: activitiesGraph.DFS()) {
			ret += " -> (" + e.getType() + "," + e.getAgent() + ",";
			if (e.getQoS() == null ) {
				ret += "NoQoS";
			} else {
				ret += e.getQoS().getComputationTime() +")";
			}
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
	public static Workflow getLinear3(){
		Graph<WorkflowTask> graph =  Graph.create(); 
		
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.A),
				ImmutableWorkflowTask.getInstance(ServiceType.B));
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.B), 
				ImmutableWorkflowTask.getInstance(ServiceType.C));
		
		return new Workflow(graph);
	}

	public static Workflow getLinear2() {
		Graph<WorkflowTask> graph =  Graph.create(); 
		
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.A),
				ImmutableWorkflowTask.getInstance(ServiceType.B));

		return new Workflow(graph);
	}

	public static Workflow getInvertedLinear2() {
		Graph<WorkflowTask> graph =  Graph.create(); 
		
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.A),
				ImmutableWorkflowTask.getInstance(ServiceType.B));
		
		return new Workflow(graph);
	}
	
	public static Workflow getLinearInverted1(){
		Graph<WorkflowTask> graph =  Graph.create(); 
		
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.B), ImmutableWorkflowTask.getInstance(ServiceType.A));
		
		return new Workflow(graph);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activitiesGraph == null) ? 0 : activitiesGraph.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Workflow other = (Workflow) obj;
		if (activitiesGraph == null) {
			if (other.activitiesGraph != null)
				return false;
		} else if (!activitiesGraph.equals(other.activitiesGraph))
			return false;
		return true;
	}

	

}
