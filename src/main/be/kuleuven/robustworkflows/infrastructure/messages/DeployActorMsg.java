package be.kuleuven.robustworkflows.infrastructure.messages;

import java.io.Serializable;

import akka.actor.Props;

public class DeployActorMsg implements Serializable {

	private static final long serialVersionUID = 201301311L;

	private final String name;
	private final Props props;
	
	public DeployActorMsg(Props props, String name) {
		if(props == null || name == null) {
			throw new IllegalArgumentException("Props and name should be set");
		}
		
		this.props = props;
		this.name = name;
	}
	
	public static DeployActorMsg valueOf(Object message) {
		return (DeployActorMsg) message;
	}


	public String getName() {
		return name;
	}
	
	public Props getProps() {
		return props;
	}

}
