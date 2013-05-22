package be.kuleuven.robustworkflows.model;

import java.io.Serializable;

import org.gephi.graph.api.Attributes;

import be.kuleuven.robustworkflows.model.factoryagent.ComputationalResourceProfile;
import be.kuleuven.robustworkflows.model.factoryagent.ExponentialProfile;

/**
 * This class acts as an interface between the GEPHI attributes and RobustWorkflows agent attributes
 * Another way to see is that this class connects two different models, one from Gephi and another
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

	private AgentAttributes(String agentType, String agentId, ComputationalResourceProfile profile) {
		this.agentType = agentType;
		this.agentId = agentId;
		this.profile = profile;
	}

	public static AgentAttributes getInstance(Attributes attributes, String nodeId) {
		String nodeType = (String) attributes.getValue(NodeAttributes.NodeType);
		ComputationalResourceProfile profile = null;
		
		//FIXME TODO parse the nodeAttribute and load the correct type of resource
		if ("Exponential".equals((String) attributes.getValue(NodeAttributes.ComputationalResourceProfile))) {
			profile = ExponentialProfile.exponential(Integer.valueOf((String) attributes.getValue("Seed")));
		}
		
		return new AgentAttributes(nodeType, nodeId, profile);
		
	}

	public String getAgentType() {
		return agentType;
	}

	public String getAgentId() {
		return agentId;
	}

	public ComputationalResourceProfile getComputationalProfile() {
		return profile;
	}
	
}
