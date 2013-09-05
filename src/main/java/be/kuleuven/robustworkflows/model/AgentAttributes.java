package be.kuleuven.robustworkflows.model;

import java.io.Serializable;

import org.gephi.graph.api.Attributes;

import be.kuleuven.robustworkflows.model.factoryagent.ComputationalResourceProfile;

/**
 * This class acts as an interface between the GEPHI attributes and RobustWorkflows agent attributes
 * Another way to see it, is that this class connects two different models, one from Gephi and another
 * from RobustWorkflows.
 * 
 * FIXME this class is also "parsing" configuration ...
 * 
 * @author mario
 *
 */
public class AgentAttributes implements Serializable {
	
	private static final long serialVersionUID = 2013L;
	
	private String agentType;
	private String agentId;
	private ComputationalResourceProfile profile;

	private long explorationStateTimeout;

	private long antExplorationTimeout;

	private AgentAttributes(String agentType, String agentId, ComputationalResourceProfile profile, long explorationStateTimeout, long antExplorationTimeout) {
		this.agentType = agentType;
		this.agentId = agentId;
		this.profile = profile;
		this.explorationStateTimeout = explorationStateTimeout;
		this.antExplorationTimeout = antExplorationTimeout;
	}


	public String getAgentType() {
		return agentType;
	}

	public String getAgentId() {
		return agentId;
	}
	
	public long getExplorationStateTimeout() {
		return explorationStateTimeout;
	}
	
	public long getAntExplorationTimeout() {
		return antExplorationTimeout;
	}

	public ComputationalResourceProfile getComputationalProfile() {
		return profile;
	}

	//FIXME I can use a abstract factory here... but, which are the implications for sending this over the wire?
	public static AgentAttributes getInstance(Attributes attributes, String nodeId) {
		String nodeType = (String) attributes.getValue(NodeAttributes.NodeType);
		ComputationalResourceProfile profile = null;
		long antExplorationTimeout=0;
		long explorationStateTimeout=0;
		
		//FIXME TODO parse the nodeAttribute and load the correct type of resource
		if ("Factory".equals((String) attributes.getValue(NodeAttributes.NodeType))) {
			
			if ("Exponential".equals((String) attributes.getValue(NodeAttributes.ComputationalResourceProfile))) {
				profile = ComputationalResourceProfile.exponential(Integer.valueOf((String) attributes.getValue("Seed")));
				
			} else if ("FixedProcessingTime".equals((String) attributes.getValue(NodeAttributes.ComputationalResourceProfile))) {
				ServiceType st = ServiceType.valueOf((String) attributes.getValue(NodeAttributes.ServiceType));
				profile = ComputationalResourceProfile.fixedProcessingTime(Integer.valueOf((String) attributes.getValue(NodeAttributes.ProcessingTimePerRequest)), st);
				
			} 
		} else if ("Client".equals((String) attributes.getValue(NodeAttributes.NodeType))) {
			explorationStateTimeout = (Long) attributes.getValue(NodeAttributes.ExplorationStateTimeout);
			antExplorationTimeout = (Long) attributes.getValue(NodeAttributes.AntExplorationTimeout);
		}
		
		return new AgentAttributes(nodeType, nodeId, profile, explorationStateTimeout, antExplorationTimeout);
	}
}
