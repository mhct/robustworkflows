package be.kuleuven.robustworkflows.model.ant;

import java.util.Set;

import be.kuleuven.robustworkflows.model.ServiceType;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Represents a workflow specification
 * @author mariohct
 *
 */
public class WorkflowServiceMatcher {
	private final Multimap<WorkflowServiceMatcherTask, WorkflowServiceMatcherTask> workflow;
	private Set<ServiceType> remainingWorkflowTasks;
	
	private WorkflowServiceMatcher(Multimap<WorkflowServiceMatcherTask, WorkflowServiceMatcherTask> workflow) {
		this.workflow = workflow;
	}
	
	public Set<ServiceType> getNeededServiceTypes() {
		return Sets.newHashSet(remainingWorkflowTasks);
	}
	
	public static WorkflowServiceMatcher getLinear1(){
		Multimap<WorkflowServiceMatcherTask, WorkflowServiceMatcherTask> workflow = ArrayListMultimap.create();
		workflow.put(WorkflowServiceMatcherTask.getInstance(ServiceType.A), WorkflowServiceMatcherTask.getInstance(ServiceType.B));
		
		return new WorkflowServiceMatcher(workflow);
	}
	
	public static WorkflowServiceMatcher getLinear(ServiceType... types) {
		Multimap<WorkflowServiceMatcherTask, WorkflowServiceMatcherTask> workflow = ArrayListMultimap.create();
		for (int i=0; i<types.length-1; i++) {
			workflow.put(WorkflowServiceMatcherTask.getInstance(types[i]), WorkflowServiceMatcherTask.getInstance(types[i+1]));
		}
		
		return new WorkflowServiceMatcher(workflow);
	}
}
