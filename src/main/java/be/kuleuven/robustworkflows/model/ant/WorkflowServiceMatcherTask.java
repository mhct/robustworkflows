package be.kuleuven.robustworkflows.model.ant;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

public class WorkflowServiceMatcherTask {
	private final ServiceType type;
	private ActorRef agent;
	private ExplorationReply qos;
	
	private WorkflowServiceMatcherTask(ServiceType type) {
		this.type = type;
	}

	public void setAgent(ActorRef actor) {
		if (actor == null) {
			throw new IllegalArgumentException("This task already has a matching service, or actor is null");
		} else {
			this.agent = actor;
		}
	}
	
	public void addQoS(ExplorationReply qos) {
		if (qos == null) {
			throw new IllegalArgumentException("QoS can't be null");
		} else {
			this.qos = qos;
		}
	}
	
	public ServiceType getType() {
		return type;
	}
	
	public ActorRef agent() {
		return agent;
	}
	
	public ExplorationReply getQos() {
		return qos;
	}
	
	public static WorkflowServiceMatcherTask getInstance(ServiceType type) {
		if (type == null) {
			throw new IllegalArgumentException("ServiceType can't be null");
		}
		
		return new WorkflowServiceMatcherTask(type);
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
		WorkflowServiceMatcherTask other = (WorkflowServiceMatcherTask) obj;
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
