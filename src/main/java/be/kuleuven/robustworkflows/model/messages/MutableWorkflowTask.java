package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;

/**
 * A task in a workflow.
 * 
 * @author mario
 *
 */
public class MutableWorkflowTask implements WorkflowTask, Serializable {
	private static final long serialVersionUID = 20130821L;
	
	private ServiceType type;
	private ActorRef agent;
	private ExplorationReply qos;

	private MutableWorkflowTask(ServiceType type) {
		this.type = type;
		this.agent = null;
		this.qos = null;
	}

	private MutableWorkflowTask(ServiceType serviceType, ActorRef agent, ExplorationReply qos) {
		this.type = serviceType;
		this.agent = agent;
		this.qos = qos;
	}

	
	
	public ServiceType getType() {
		return type;
	}

	public void setType(ServiceType type) {
		this.type = type;
	}

	public ActorRef getAgent() {
		return agent;
	}

	public void setAgent(ActorRef agent) {
		this.agent = agent;
	}

	public ExplorationReply getQoS() {
		return qos;
	}

	public void setQoS(ExplorationReply qos) {
		this.qos = qos;
	}

	@Override
	public String toString() {
		return "WorkflowTask [type=" + type + ", agent=" + agent + ", qos="
				+ qos + "]";
	}

	public static MutableWorkflowTask getInstance(ServiceType type) {
		return new MutableWorkflowTask(type);
	}

	/**
	 * Factory Method to create a WorkflowTask
	 * 
	 * @param serviceType
	 * @param agent
	 * @param qos
	 * @return
	 */
	public static MutableWorkflowTask getInstance(ServiceType serviceType, ActorRef agent, ExplorationReply qos) {
		return new MutableWorkflowTask(serviceType, agent, qos);
	}

	@Override
	public WorkflowTask getImmutableWorkflowTask() {
		return this;
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
		MutableWorkflowTask other = (MutableWorkflowTask) obj;
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
	
	
}
