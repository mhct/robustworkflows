package be.kuleuven.robustworkflows.model.ant;

import java.util.Map;
import java.util.Set;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ImmutableWorkflowTask;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * Represents a workflow specification
 * 
 * @Mutable
 * @author mariohct
 *
 */
public class WorkflowServiceMatcher {
	private final Multimap<MutableWorkflowTask, MutableWorkflowTask> workflow;
	private Set<ServiceType> remainingWorkflowTasks = Sets.newTreeSet();
	
	private WorkflowServiceMatcher(Multimap<MutableWorkflowTask, MutableWorkflowTask> workflow) {
		this.workflow = workflow;
		initializeRemainingWorkflowTasks();
	}
	
	/**
	 * Parses the graph given by workflow and adds the required ServiceTypes to the
	 * remainingWorkflowTasks attribute
	 */
	private void initializeRemainingWorkflowTasks() {
		for (Map.Entry<MutableWorkflowTask, MutableWorkflowTask> e: workflow.entries()) {
			remainingWorkflowTasks.add(e.getKey().getType());
			remainingWorkflowTasks.add(e.getValue().getType());
		}
	}
	
	
	/**
	 * Returns a set containing all ServiceTypes needed by the tasks of this workflow
	 * @return
	 */
	public Set<ServiceType> getNeededServiceTypes() {
		return Sets.newTreeSet(remainingWorkflowTasks);
	}

	/**
	 * Associates an Agent and QoS to a particular TASK from the workflow. Current it uses the ServiceRequestExplorationReply.ServiceType to match which task is it...
	 * @param agent
	 * @param qos
	 */
	public void associateAgentToTask(ActorRef agent, ExplorationReply qos) {
		
		MutableWorkflowTask task = null;
		for (Map.Entry<MutableWorkflowTask, MutableWorkflowTask> e: workflow.entries()) {
			if (e.getKey().getType().equals(qos.getRequestExploration().getServiceType())) {
				task = e.getKey();
				break;
			} else if (e.getValue().getType().equals(qos.getRequestExploration().getServiceType())) {
				task = e.getValue();
				break;
			}
		}
		
		if (task != null) {
			task.setAgent(agent);
			task.addQoS(qos);
			remainingWorkflowTasks.remove(qos.getRequestExploration().getServiceType());
		}
	}
	
	/**
	 * Returns the RAW representatiom (the graph representing) this workflow
	 * @return
	 */
	public Multimap<MutableWorkflowTask, MutableWorkflowTask> rawWorkflow() {
		return LinkedListMultimap.create(workflow);
	}
	
	/**
	 * Creates a representation of this datastructure as a Workflow message
	 * 
	 * @return an immutable workflow message representing this workflow
	 */
	public Workflow createWorkflow() {
		Multimap<WorkflowTask, WorkflowTask> workflowMessage = LinkedListMultimap.create();
		
		for (Map.Entry<MutableWorkflowTask, MutableWorkflowTask> e: workflow.entries()) {
			WorkflowTask key = ImmutableWorkflowTask.getInstance(e.getKey().getType(), e.getKey().getAgent(), e.getKey().getQoS());
			WorkflowTask value = ImmutableWorkflowTask.getInstance(e.getValue().getType(), e.getValue().getAgent(), e.getValue().getQoS());
			workflowMessage.put(key, value);
		}
		
		return Workflow.getInstance(workflowMessage);
	}
	
	/**
	 * Factory Method that creates a WorkflowServiceMatcher based on the 
	 * workflow description Workflow
	 * 
	 * @param workflow the skeleton workflow
	 * @return an instance of the WorkflowServiceMacher
	 */
	public static WorkflowServiceMatcher getInstance(Workflow workflow) {
		Multimap<MutableWorkflowTask, MutableWorkflowTask> workflowSM = LinkedListMultimap.create();
		
		for (Map.Entry<WorkflowTask, WorkflowTask> e: workflow.rawWorkflow().entries()) {
			workflowSM.put(MutableWorkflowTask.getInstance(e.getKey().getType()), MutableWorkflowTask.getInstance(e.getValue().getType()));
		}
		
		return new WorkflowServiceMatcher(workflowSM);
	}
	
//	/**
//	 * Factory Method that creates simple workflow A -> B
//	 * 
//	 * @return
//	 */
//	public static WorkflowServiceMatcher getLinear1(){
//		Multimap<WorkflowServiceMatcherTask, WorkflowServiceMatcherTask> workflow = LinkedListMultimap.create();
//		workflow.put(WorkflowServiceMatcherTask.getInstance(ServiceType.A), WorkflowServiceMatcherTask.getInstance(ServiceType.B));
//		
//		return new WorkflowServiceMatcher(workflow);
//	}
//	
//	/**
//	 * Factory Method based on the service types needed.
//	 * TODO perhaps remove this.. since this type olny exists because of Workflow
//	 * @param types
//	 * @return
//	 */
//	public static WorkflowServiceMatcher getLinear(ServiceType... types) {
//		Multimap<WorkflowServiceMatcherTask, WorkflowServiceMatcherTask> workflow = LinkedListMultimap.create();
//		for (int i=0; i<types.length-1; i++) {
//			workflow.put(WorkflowServiceMatcherTask.getInstance(types[i]), WorkflowServiceMatcherTask.getInstance(types[i+1]));
//		}
//		
//		return new WorkflowServiceMatcher(workflow);
//	}
}
