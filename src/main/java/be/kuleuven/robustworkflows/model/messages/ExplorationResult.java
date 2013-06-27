package be.kuleuven.robustworkflows.model.messages;

import java.util.Iterator;

import be.kuleuven.robustworkflows.model.ant.WorkflowServiceMatcher;

/**
 * Holds the exploration data obtained by an ExplorationAnt
 * 
 * @author mario
 *
 */
public class ExplorationResult implements Iterable<WorkflowTask> {
	//FIXME the result should be in terms of Workflow and selected service to play that task in the workflow
//	private final Map<ActorRef, ServiceRequestExplorationReply> data;
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

//	public Map<ActorRef, ServiceType> getAgentServicesMap() {
//		Map<ActorRef, ServiceType> agentServices = Maps.newTreeMap();
//		
//		for (Map.Entry<ActorRef, ServiceRequestExplorationReply> e: data.entrySet()) {
//			agentServices.put(e.getKey(), e.getValue().getServiceType());
//		}
//		
//		return agentServices;
//	}
	
//	public Set<ActorRef> getFactoryAgents() {
//		return new HashSet<ActorRef>(data.keySet());
//	}

//	public static ExplorationResult getInstance(Map<ActorRef, ServiceRequestExplorationReply> data) {
//		return new ExplorationResult(Maps.newHashMap(data));
//	}

	public static ExplorationResult getInstance(WorkflowServiceMatcher workflow) {
		return new ExplorationResult(workflow.createWorkflow());
	}

	/**
	 * Iterates the workflow using Breadth First search
	 * 
	 * @return
	 */
	@Override
	public Iterator<WorkflowTask> iterator() {
		Iterator<WorkflowTask> itr = new Iterator<WorkflowTask>() {

			private WorkflowTask current = workflow.rawWorkflow().keySet().iterator().next(); //starts at the head
			
			@Override
			public boolean hasNext() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public WorkflowTask next() {
				if (current != null) {
					
				}
			}

			@Override
			public void remove() {
				// TODO Auto-generated method stub
				
			}
			
		}
	}
}
