package be.kuleuven.robustworkflows.util;

import java.util.Map;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.NodeDraft;

import be.kuleuven.robustworkflows.model.NodeAttributeValues;
import be.kuleuven.robustworkflows.model.NodeAttributes;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgent;
import be.kuleuven.robustworkflows.model.factoryagent.FactoryAgent;
import be.kuleuven.robustworkflows.model.messages.Workflow;

import com.google.common.collect.Maps;

/**
 * This class represents a NodeDraft factory for nodes that represent agents.
 * Agents can be either from the type Factory (Component Service) or Client (Composite Service).
 * 
 * @author mario
 *
 */
public class AgentNodesCreator {
	private RandomDataGenerator random2;
	private RandomDataGenerator randomWorkflows;
	private RandomDataGenerator seedGen;
	private RandomDataGenerator randomP1;
	private RandomDataGenerator randomP2;
	private RandomDataGenerator randomP3;

	private final ContainerLoader container;
	private final float percentageClients;
	
	Map<String, AttributeColumn> columns;

	private final long EXPLORATION_TIMEOUT;
	private final long ANT_EXPLORATION_TIMEOUT;
	private final double ANT_SAMPLING_PROBABILITY;
	
	/**
	 * Instantiates the nodes factory.
	 * 
	 * @param ratio, the percentage of ClientAgents, from the total number of nodes, one wants to have
	 * @param container, a Gephi container
	 * 
	 * @return an AgentNodesCreator factory
	 */
	public AgentNodesCreator(float percentageClients, ContainerLoader container) {
		this.percentageClients = percentageClients;
		this.container = container;
		long seed = System.currentTimeMillis();
		
		random2 = new RandomDataGenerator(new MersenneTwister(seed));
		randomWorkflows = new RandomDataGenerator(new MersenneTwister(seed));
		seedGen = new RandomDataGenerator(new MersenneTwister(seed));
		randomP1 = new RandomDataGenerator(new MersenneTwister(2*seed));
		randomP2 = new RandomDataGenerator(new MersenneTwister(3*seed));
		randomP3 = new RandomDataGenerator(new MersenneTwister(4*seed));
		
		this.EXPLORATION_TIMEOUT = 2050;
		this.ANT_EXPLORATION_TIMEOUT = 1950;
		this.ANT_SAMPLING_PROBABILITY = 1.0;

		configureNeededColumns(container);
	}
	
	/**
	 * Adds the needed column types representing the agent attributes that have to be in the graph
	 * 
	 * @param container
	 */
	private void configureNeededColumns(ContainerLoader container) {
		columns = Maps.newHashMap();
		AttributeTable nodeTable = container.getAttributeModel().getNodeTable();
		
		columns.put(NodeAttributes.NodeType, nodeTable.addColumn(NodeAttributes.NodeType, AttributeType.STRING));
		columns.put(NodeAttributes.ComputationalResourceProfile, nodeTable.addColumn(NodeAttributes.ComputationalResourceProfile, AttributeType.STRING));
		columns.put(NodeAttributes.Seed, nodeTable.addColumn(NodeAttributes.Seed, AttributeType.INT)); 
		columns.put(NodeAttributes.Sigma, nodeTable.addColumn(NodeAttributes.Sigma, AttributeType.DOUBLE));               
		
		//FIXME the type of processingtimeperrequest is string, SHOULD be INT or long..
		columns.put(NodeAttributes.ProcessingTimePerRequest, nodeTable.addColumn(NodeAttributes.ProcessingTimePerRequest, AttributeType.STRING));
		columns.put(NodeAttributes.ServiceType, nodeTable.addColumn(NodeAttributes.ServiceType, AttributeType.STRING));
		
        
		columns.put(NodeAttributes.ExplorationStateTimeout, nodeTable.addColumn(NodeAttributes.ExplorationStateTimeout, AttributeType.LONG));                                    
		columns.put(NodeAttributes.AntExplorationTimeout, nodeTable.addColumn(NodeAttributes.AntExplorationTimeout, AttributeType.LONG));                                  
		columns.put(NodeAttributes.AntExplorationSamplingProbability, nodeTable.addColumn(NodeAttributes.AntExplorationSamplingProbability, AttributeType.DOUBLE));                     
		columns.put(NodeAttributes.ExplorationBehaviorFactory, nodeTable.addColumn(NodeAttributes.ExplorationBehaviorFactory, AttributeType.STRING));      
		columns.put(NodeAttributes.WorkflowFactory, nodeTable.addColumn(NodeAttributes.WorkflowFactory, AttributeType.STRING));                                                
	}
	
	/**
	 * Returns a new NodeDraft, having attributes to represent either a FactoryAgent or a ClientAgent, 
	 * according to the desired probability given at the instantiation of this class.
	 * 
	 * @return
	 */
	public NodeDraft newNodeDraft() {
		if (random2.nextUniform(0.0, 1.0) >= percentageClients) {
			return newFactoryNode();
		} else {
			return newClientNode();
		}
	}
	
	/**
	 * Creates a NodeDraft having attributes of a {@link FactoryAgent}
	 * 
	 * @return NodeDraft
	 */
	private NodeDraft newFactoryNode() {
		NodeDraft n = container.factory().newNodeDraft();
				
		int processingTimePerRequest;
		double e = random2.nextUniform(0, 1);

		String serviceType = ServiceType.A.toString();
		if (e >= 1.0/3.0 && e < 2.0/3.0) {
			serviceType = ServiceType.B.toString();
			processingTimePerRequest = randomP1.nextInt(10000, 30000);
		} else if (e >= 2.0/3.0) {
			serviceType = ServiceType.C.toString();
			processingTimePerRequest = randomP2.nextInt(10000, 30000);
		} else {
			processingTimePerRequest = randomP3.nextInt(10000, 30000);
		}
		n.addAttributeValue(columns.get(NodeAttributes.NodeType), NodeAttributeValues.Factory);
		n.addAttributeValue(columns.get(NodeAttributes.ComputationalResourceProfile), NodeAttributeValues.AverageDistributionProcessingTimeProfile);
		n.addAttributeValue(columns.get(NodeAttributes.Seed), seedGen.nextInt(1000, 90000));
		n.addAttributeValue(columns.get(NodeAttributes.Sigma), 0.7);
		n.addAttributeValue(columns.get(NodeAttributes.ProcessingTimePerRequest), String.valueOf(processingTimePerRequest));
		n.addAttributeValue(columns.get(NodeAttributes.ServiceType), serviceType);

		return n;
	}
	
	/**
	 * Creates a NodeDraft having attributes to represent a {@link ClientAgent}.
	 * The NodeDraft receives an attribute to create ClientAgent that represent a composite service with a {@link Workflow} that can be,
	 * Linear2, Linear3, or InvertedLinear2.
	 * 
	 * @return NodeDraft
	 */
	private NodeDraft newClientNode() {
		NodeDraft n = container.factory().newNodeDraft();
		double e = randomWorkflows.nextUniform(0.0, 1.0);
		
		String workflowFactory = NodeAttributeValues.Linear3;
		if (e >= 1.0/3.0 && e < 2.0/3.0) {
			workflowFactory = NodeAttributeValues.Linear2;
		} else if (e >= 2.0/3.0) {
			workflowFactory = NodeAttributeValues.InvertedLinear2;
		}
		
		n.addAttributeValue(columns.get(NodeAttributes.NodeType), NodeAttributeValues.Client);
		n.addAttributeValue(columns.get(NodeAttributes.ExplorationStateTimeout), EXPLORATION_TIMEOUT);
		n.addAttributeValue(columns.get(NodeAttributes.AntExplorationTimeout), ANT_EXPLORATION_TIMEOUT);
		n.addAttributeValue(columns.get(NodeAttributes.AntExplorationSamplingProbability), ANT_SAMPLING_PROBABILITY);
		n.addAttributeValue(columns.get(NodeAttributes.ExplorationBehaviorFactory), NodeAttributeValues.SimpleExplorationBehaviour);
		n.addAttributeValue(columns.get(NodeAttributes.WorkflowFactory), workflowFactory);
		
		return n;
	}
}

