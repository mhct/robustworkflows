package be.kuleuven.robustworkflows.model.ant;

import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.QoSData;

public class WorkflowServiceMatcherTask {
	private final ServiceType type;
	private ActorRef agent;
	private QoSData qos;
	
	private WorkflowServiceMatcherTask(ServiceType type) {
		this.type = type;
	}

	public void setAgent(ActorRef actor) {
		if (this.agent == null && actor != null) {
			this.agent = actor;
		} else {
			throw new IllegalArgumentException("This task already has a matching service, or actor is null");
		}
	}
	
	public void addQoS(QoSData qos) {
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
	
	public QoSData getQos() {
		return qos;
	}
	
	public static WorkflowServiceMatcherTask getInstance(ServiceType type) {
		if (type == null) {
			throw new IllegalArgumentException("ServiceType can't be null");
		}
		
		return new WorkflowServiceMatcherTask(type);
	}
}
