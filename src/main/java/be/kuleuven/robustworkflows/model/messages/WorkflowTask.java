package be.kuleuven.robustworkflows.model.messages;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * A task in a workflow.
 * 
 * @author mario
 *
 */
public class WorkflowTask {
	private ServiceType type;
	private ActorRef agent;
	private ExplorationReply qos;

	private WorkflowTask(ServiceType type) {
		this.type = type;
	}

	private WorkflowTask(ServiceType serviceType, ActorRef agent, ExplorationReply qos) {
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

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((agent == null) ? 0 : agent.hashCode());
		result = prime * result + ((qos == null) ? 0 : qos.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		WorkflowTask other = (WorkflowTask) obj;
		if (agent == null) {
			if (other.agent != null)
				return false;
		} else if (!agent.equals(other.agent))
			return false;
		if (qos == null) {
			if (other.qos != null)
				return false;
		} else if (!qos.equals(other.qos))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	public static WorkflowTask getInstance(ServiceType type) {
		return new WorkflowTask(type);
	}

	/**
	 * Factory Method to create a WorkflowTask
	 * 
	 * @param serviceType
	 * @param agent
	 * @param qos
	 * @return
	 */
	public static WorkflowTask getInstance(ServiceType serviceType, ActorRef agent, ExplorationReply qos) {
		return new WorkflowTask(serviceType, agent, qos);
	}
	
}
