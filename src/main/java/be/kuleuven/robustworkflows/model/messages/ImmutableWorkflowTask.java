package be.kuleuven.robustworkflows.model.messages;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * A task in a workflow.
 * 
 * @author mario
 *
 */
public class ImmutableWorkflowTask implements WorkflowTask {
	
	private final ServiceType type;
	private final ActorRef agent;
	private final ExplorationReply qos;

	private ImmutableWorkflowTask(ServiceType type) {
		this.type = type;
		this.agent = null;
		this.qos = null;
	}

	private ImmutableWorkflowTask(ServiceType serviceType, ActorRef agent, ExplorationReply qos) {
		this.type = serviceType;
		this.agent = agent;
		this.qos = qos;
	}

	public ServiceType getType() {
		return type;
	}
	
	public ActorRef getAgent() {
		return agent;
	}
	
	public ExplorationReply getQoS() {
		return qos;
	}
	
	@Override
	public String toString() {
		return "WorkflowTask [type=" + type + ", agent=" + agent + ", qos="
				+ qos + "]";
	}

	public static ImmutableWorkflowTask getInstance(ServiceType type) {
		return new ImmutableWorkflowTask(type);
	}

	/**
	 * Factory Method to create a WorkflowTask
	 * 
	 * @param serviceType
	 * @param agent
	 * @param qos
	 * @return
	 */
	public static ImmutableWorkflowTask getInstance(ServiceType serviceType, ActorRef agent, ExplorationReply qos) {
		return new ImmutableWorkflowTask(serviceType, agent, qos);
	}

	@Override
	public WorkflowTask getImmutableWorkflowTask() {
		return this;
	}
	
}
