package be.kuleuven.robustworkflows.model.messages;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * A task in a workflow.
 * 
 * @author mario
 *
 */
public interface WorkflowTask {
	public ServiceType getType();
	public ActorRef getAgent();
	public ExplorationReply getQoS();
	public String toString();
	public WorkflowTask getImmutableWorkflowTask();
}
