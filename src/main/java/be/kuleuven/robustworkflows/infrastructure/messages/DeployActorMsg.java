package be.kuleuven.robustworkflows.infrastructure.messages;

import java.io.Serializable;

import akka.actor.Props;

public class DeployActorMsg implements Serializable {

	private static final long serialVersionUID = 201301311L;

	private final String name;
	private Props props; //TODO removing it

	private String agentType;
	
	public DeployActorMsg(Props props, String name) {
		if(props == null || name == null) {
			throw new IllegalArgumentException("Props and name should be set");
		}
		
		this.props = props;
		this.name = name;
	}

	public DeployActorMsg(String name) {
		if(name == null) {
			throw new IllegalArgumentException("Name should be set");
		}
		
		this.name = name;
	}
	
	public DeployActorMsg(String agentType, String name) {
		if(agentType == null || name == null) {
			throw new IllegalArgumentException("Props and name should be set");
		}
		
		this.agentType = agentType;
		this.name = name;
	}

	public static DeployActorMsg valueOf(Object message) {
		return (DeployActorMsg) message;
	}

	public String getAgentType() {
		return agentType;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "DeployActorMsg [name=" + name + ", agentType=" + agentType
				+ "]";
	}
	
	
	/**
	 * {@link Deprecated}
	 * @return
	 */
//	public Props getProps(DB db) {
////		return props;
//		return new Props(new UntypedActorFactory() {
//			
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 2013021401L;
//
//			@Override
//			public Actor create() throws Exception {
//				return new FactoryAgent(adminDB);
//			}
//		}
//	}

}
