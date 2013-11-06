package be.kuleuven.robustworkflows.model;

import java.io.Serializable;

import org.gephi.graph.api.Attributes;

import be.kuleuven.robustworkflows.model.clientagent.CompositeExplorationFactory;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationBehaviorFactory;
import be.kuleuven.robustworkflows.model.clientagent.SimpleExplorationFactory;
import be.kuleuven.robustworkflows.model.factoryagent.ComputationalResourceProfile;
import be.kuleuven.robustworkflows.model.messages.Workflow;

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
	//FIXME refactor this out to a ExplorationAntXMLParameter class
	private long antExplorationTimeout;
	private double antExplorationSamplingProbability;
	private ExplorationBehaviorFactory behaviorFactory;
	private Workflow workflow;


	private AgentAttributes(String agentType, 
			String agentId, 
			ComputationalResourceProfile profile, 
			long explorationStateTimeout, 
			long antExplorationTimeout, 
			double antExplorationSamplingProbability, 
			ExplorationBehaviorFactory behaviorFactory,
			Workflow workflow) {

		this.agentType = agentType;
		this.agentId = agentId;
		this.profile = profile;
		this.explorationStateTimeout = explorationStateTimeout;
		this.antExplorationTimeout = antExplorationTimeout;
		this.antExplorationSamplingProbability = antExplorationSamplingProbability;
		this.behaviorFactory = behaviorFactory;
		this.workflow = workflow;
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

	public ExplorationBehaviorFactory getBehaviorFactory() {
		return behaviorFactory;
	}
	
	public double getAntExplorationSamplingProbability() {
		return antExplorationSamplingProbability;
	}
	
	public Workflow getWorkflow() {
		return workflow;
	}
	
	//FIXME I can use a abstract factory here... but, which are the implications for sending this over the wire?
	public static AgentAttributes getInstance(Attributes attributes, String nodeId) {
		String nodeType = (String) attributes.getValue(NodeAttributes.NodeType);
		ComputationalResourceProfile profile = null;
		long antExplorationTimeout=0;
		double antExplorationSamplingProbability=0;
		long explorationStateTimeout=0;
		ExplorationBehaviorFactory behaviorFactory = null;
		Workflow workflow = null;
		
		//FIXME TODO parse the nodeAttribute and load the correct type of resource
		if (NodeAttributeValues.Factory.equals((String) attributes.getValue(NodeAttributes.NodeType))) {
			
			if (NodeAttributeValues.Exponential.equals((String) attributes.getValue(NodeAttributes.ComputationalResourceProfile))) {
				profile = ComputationalResourceProfile.exponential(Integer.valueOf((String) attributes.getValue("Seed")));
				
			} else if (NodeAttributeValues.FixedProcessingTime.equals((String) attributes.getValue(NodeAttributes.ComputationalResourceProfile))) {
				ServiceType st = ServiceType.valueOf((String) attributes.getValue(NodeAttributes.ServiceType));
				profile = ComputationalResourceProfile.fixedProcessingTime(Integer.valueOf((String) attributes.getValue(NodeAttributes.ProcessingTimePerRequest)), st);
				
			} else if (NodeAttributeValues.AverageDistributionProcessingTimeProfile.equals((String) attributes.getValue(NodeAttributes.ComputationalResourceProfile))) {
				ServiceType st = ServiceType.valueOf((String) attributes.getValue(NodeAttributes.ServiceType));
				profile = ComputationalResourceProfile.average(Integer.valueOf((String) attributes.getValue(NodeAttributes.ProcessingTimePerRequest)), 
						(Double) attributes.getValue(NodeAttributes.Sigma),
						(Integer) attributes.getValue(NodeAttributes.Seed),
						st);
			
			} 
		} else if (NodeAttributes.Client.equals((String) attributes.getValue(NodeAttributes.NodeType))) {
			explorationStateTimeout = (Long) attributes.getValue(NodeAttributes.ExplorationStateTimeout);
			antExplorationTimeout = (Long) attributes.getValue(NodeAttributes.AntExplorationTimeout);
			antExplorationSamplingProbability =  (Double) attributes.getValue(NodeAttributes.AntExplorationSamplingProbability);
			
			//ant behavior
			if (SimpleExplorationFactory.class.getName().equals(attributes.getValue(NodeAttributes.ExplorationBehaviorFactory))) {
				behaviorFactory = new SimpleExplorationFactory();
			} else {
				behaviorFactory = new CompositeExplorationFactory();
			}
			
			//workflows to be used by ClientAgents
			if (NodeAttributeValues.Linear2.equals(attributes.getValue(NodeAttributes.WorkflowFactory))) {
				workflow = Workflow.getLinear2();
			} else if (NodeAttributeValues.Linear3.equals(attributes.getValue(NodeAttributes.WorkflowFactory))) {
				workflow = Workflow.getLinear3();
			} else if (NodeAttributeValues.InvertedLinear2.equals(attributes.getValue(NodeAttributes.WorkflowFactory))) {
				workflow = Workflow.getInvertedLinear2();
			}
			
		}
		
		return new AgentAttributes(nodeType, 
				nodeId, 
				profile, 
				explorationStateTimeout, 
				antExplorationTimeout, 
				antExplorationSamplingProbability, 
				behaviorFactory,
				workflow);
	}


	
}
