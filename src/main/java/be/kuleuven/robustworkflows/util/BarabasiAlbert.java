package be.kuleuven.robustworkflows.util;

/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.Report;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import be.kuleuven.robustworkflows.model.NodeAttributeValues;
import be.kuleuven.robustworkflows.model.NodeAttributes;
import be.kuleuven.robustworkflows.model.ServiceType;

import com.google.common.collect.Maps;

/**
 * Generates an undirected connected graph.
 *
 * http://en.wikipedia.org/wiki/Barabási–Albert_model
 * http://www.barabasilab.com/pubs/CCNR-ALB_Publications/199910-15_Science-Emergence/199910-15_Science-Emergence.pdf
 * http://www.facweb.iitkgp.ernet.in/~niloy/COURSE/Spring2006/CNT/Resource/ba-model-2.pdf
 *
 * N  > 0
 * m0 > 0 && m0 <  N
 * M  > 0 && M  <= m0
 *
 * O(N^2 * M)
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = Generator.class)
public class BarabasiAlbert implements Generator {
	private boolean cancel = false;
	private ProgressTicket progressTicket;

	private int N = 0;
	private int m0 = 1;
	private int M  = 1;
	
	private boolean considerExistingNodes;
	
	private AgentNodesCreator agentsCreator;
	
	
	@Override
	public void generate(ContainerLoader container) {
		
		Progress.start(progressTicket, m0 + (N - m0) * M);
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
		container.setEdgeDefault(EdgeDefault.UNDIRECTED);

		agentsCreator = new AgentNodesCreator(container);
		
		
		// Timestamps
		int vt = 1;
		int et = 1;

		NodeDraft[] nodes = new NodeDraft[N];
		int[] degrees = new int[N];

		// Creating m0 nodes
		for (int i = 0; i < m0 && !cancel; ++i) {
			NodeDraft node = agentsCreator.newNodeDraft();
			node.setLabel("Node " + i);
			node.addTimeInterval("0", (N - m0) + "");
			nodes[i] = node;
			degrees[i] = 0;
			container.addNode(node);
			Progress.progress(progressTicket);
		}

		// Linking every node with each other (no self-loops)
		for (int i = 0; i < m0 && !cancel; ++i)
			for (int j = i + 1; j < m0 && !cancel; ++j) {
				EdgeDraft edge = container.factory().newEdgeDraft();
				edge.setSource(nodes[i]);
				edge.setTarget(nodes[j]);
				edge.addTimeInterval("0", (N - m0) + "");
				degrees[i]++;
				degrees[j]++;
				container.addEdge(edge);
			}

		// Adding N - m0 nodes, each with M edges
		for (int i = m0; i < N && !cancel; ++i, ++vt, ++et) {
			// Adding new node
//			NodeDraft node = container.factory().newNodeDraft();
			NodeDraft node = agentsCreator.newNodeDraft();
			node.setLabel("Node " + i);
			node.addTimeInterval(vt + "", (N - m0) + "");
			nodes[i] = node;
			degrees[i] = 0;
			container.addNode(node);

			// Adding M edges out of the new node
			double sum = 0.0; // sum of all nodes degrees
			for (int j = 0; j < i && !cancel; ++j)
				sum += degrees[j];
			double s = 0.0;
			for (int m = 0; m < M && !cancel; ++m) {
				double r = random.nextUniform(0.0, 1.0);
				double p = 0.0;
				for (int j = 0; j < i && !cancel; ++j) {
					if (container.edgeExists(nodes[i], nodes[j]) || container.edgeExists(nodes[j], nodes[i]))
						continue;

					if (i == 1)
						p = 1.0;
					else p += degrees[j] / sum + s / (i - m);

					if (r <= p) {
						s += degrees[j] / sum;

						EdgeDraft edge = container.factory().newEdgeDraft();
						edge.setSource(nodes[i]);
						edge.setTarget(nodes[j]);
						edge.addTimeInterval(et + "", (N - m0) + "");
						degrees[i]++;
						degrees[j]++;
						container.addEdge(edge);
						Progress.progress(progressTicket);

						break;
					}
				}
			}

			Progress.progress(progressTicket);
		}

		Progress.finish(progressTicket);
		progressTicket = null;
	}

	public int getN() {
		return N;
	}

	public int getm0() {
		return m0;
	}

	public int getM() {
		return M;
	}

	public void setN(int N) {
		this.N = N;
	}

	public void setm0(int m0) {
		this.m0 = m0;
	}

	public void setM(int M) {
		this.M = M;
	}

	public boolean isConsiderExistingNodes() {
		return considerExistingNodes;
	}

	public void setConsiderExistingNodes(boolean considerExistingNodes) {
		this.considerExistingNodes = considerExistingNodes;
	}

	@Override
	public String getName() {
		return "Barabasi-Albert Scale Free model";
	}

	@Override
	public GeneratorUI getUI() {
//		return Lookup.getDefault().lookup(BarabasiAlbertUI.class);
		return null;
	}

	@Override
	public boolean cancel() {
		cancel = true;
		return true;
	}

	@Override
	public void setProgressTicket(ProgressTicket progressTicket) {
		this.progressTicket = progressTicket;
	}
	
	public static void main(String[] args) {
		int nbNodes = 6;
		if (args.length >= 1) {
			nbNodes = Integer.valueOf(args[0]);
		}
		
		BarabasiAlbert generator = new BarabasiAlbert();
		generator.setM(5);
		generator.setN(nbNodes);
		generator.setm0(5);
		ContainerFactory containerFactory = Lookup.getDefault().lookup(ContainerFactory.class);
		Container container = containerFactory.newContainer();
		container.setReport(new Report());
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		
		generator.generate(container.getLoader());
		
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		importController.process(container, new DefaultProcessor(), pc.getCurrentWorkspace());
		
//		
//		for (NodeDraftGetter node: container.getUnloader().getNodes()) {
//			System.out.println("id: " + node.getId());
//			node.addAttributeValue(col, 0.8);
//		}
		
//		for (EdgeDraftGetter edge: container.getUnloader().getEdges()) {
//			System.out.println(edge.getSource().getId() + " -> " + edge.getTarget().getId());
//		}
		
		final ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			String graphFilename = "/tmp/asp.gexf";
			ec.exportFile(new File(graphFilename), pc.getCurrentWorkspace());
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}

class AgentNodesCreator {
	private RandomDataGenerator random2;
	private RandomDataGenerator randomWorkflows;
	private RandomDataGenerator seedGen;
	private RandomDataGenerator randomP1;
	private RandomDataGenerator randomP2;
	private RandomDataGenerator randomP3;

	private ContainerLoader container;
	Map<String, AttributeColumn> columns;
	
	public AgentNodesCreator(ContainerLoader container) {
		this.container = container;
		long seed = 101010;
		
		random2 = new RandomDataGenerator(new MersenneTwister(seed));
		randomWorkflows = new RandomDataGenerator(new MersenneTwister(seed));
		seedGen = new RandomDataGenerator(new MersenneTwister(seed));
		randomP1 = new RandomDataGenerator(new MersenneTwister(2*seed));
		randomP2 = new RandomDataGenerator(new MersenneTwister(3*seed));
		randomP3 = new RandomDataGenerator(new MersenneTwister(4*seed));
		
		configureNeededColumns(container);
	}
	
	public void configureNeededColumns(ContainerLoader container) {
		columns = Maps.newHashMap();
		AttributeTable nodeTable = container.getAttributeModel().getNodeTable();
		
		columns.put(NodeAttributes.NodeType, nodeTable.addColumn(NodeAttributes.NodeType, AttributeType.STRING));
		columns.put(NodeAttributes.ComputationalResourceProfile, nodeTable.addColumn(NodeAttributes.ComputationalResourceProfile, AttributeType.STRING));
		columns.put(NodeAttributes.Seed, nodeTable.addColumn(NodeAttributes.Seed, AttributeType.INT)); 
		columns.put(NodeAttributes.Sigma, nodeTable.addColumn(NodeAttributes.Sigma, AttributeType.DOUBLE));                 
		columns.put(NodeAttributes.ProcessingTimePerRequest, nodeTable.addColumn(NodeAttributes.ProcessingTimePerRequest, AttributeType.INT));
		columns.put(NodeAttributes.ServiceType, nodeTable.addColumn(NodeAttributes.ServiceType, AttributeType.STRING));
		
        
		columns.put(NodeAttributes.ExplorationStateTimeout, nodeTable.addColumn(NodeAttributes.ExplorationStateTimeout, AttributeType.INT));                                    
		columns.put(NodeAttributes.AntExplorationTimeout, nodeTable.addColumn(NodeAttributes.AntExplorationTimeout, AttributeType.INT));                                  
		columns.put(NodeAttributes.AntExplorationSamplingProbability, nodeTable.addColumn(NodeAttributes.AntExplorationSamplingProbability, AttributeType.DOUBLE));                     
		columns.put(NodeAttributes.ExplorationBehaviorFactory, nodeTable.addColumn(NodeAttributes.ExplorationBehaviorFactory, AttributeType.STRING));      
		columns.put(NodeAttributes.WorkflowFactory, nodeTable.addColumn(NodeAttributes.WorkflowFactory, AttributeType.STRING));                                                
	}
	
	/**
	 * Returns a new Node either of the type of a Factory or a Client, according to the desired probability
	 * 
	 * @return
	 */
	public NodeDraft newNodeDraft() {
		if (random2.nextUniform(0.0, 1.0) >= 0.5) {
			return newFactoryNode();
		} else {
			return newClientNode();
		}
	}
	
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
	
	private final long EXPLORATION_TIMEOUT = 2050;
	private final long ANT_EXPLORATION_TIMEOUT = 1950;
	private double ANT_SAMPLING_PROBABILITY = 1.0;
	
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
