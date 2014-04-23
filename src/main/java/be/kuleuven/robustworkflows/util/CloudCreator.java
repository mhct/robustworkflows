package be.kuleuven.robustworkflows.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

import be.kuleuven.robustworkflows.model.NodeAttributeValues;
import be.kuleuven.robustworkflows.model.NodeAttributes;
import be.kuleuven.robustworkflows.model.ServiceType;

import com.google.common.collect.Lists;

public class CloudCreator {

//	private int SEED = 8080823;
//	private RandomDataGenerator random1;
	private RandomDataGenerator random2;
	private RandomDataGenerator randomWorkflows;
	private RandomDataGenerator seedGen;
	private RandomDataGenerator randomP1;
	private RandomDataGenerator randomP2;
	private RandomDataGenerator randomP3;

	private int nbFactories;
	private final long EXPLORATION_TIMEOUT = 2050;
	private final long ANT_EXPLORATION_TIMEOUT = 1950;
	private double ANT_SAMPLING_PROBABILITY = 1.0;
	private int steps;
	
	private CloudCreator(int steps, int seed, int factories) {
		this.steps = steps;
//		random1 = new RandomDataGenerator(new MersenneTwister(seed));
		random2 = new RandomDataGenerator(new MersenneTwister(seed));
		randomWorkflows = new RandomDataGenerator(new MersenneTwister(seed));
		seedGen = new RandomDataGenerator(new MersenneTwister(seed));
		randomP1 = new RandomDataGenerator(new MersenneTwister(2*seed));
		randomP2 = new RandomDataGenerator(new MersenneTwister(3*seed));
		randomP3 = new RandomDataGenerator(new MersenneTwister(4*seed));

		nbFactories = factories;
//		NUMBER_OF_CLIENTS = clients;
//		
//		List<Integer> clients = new ArrayList<Integer>();
//		for (Integer i: factories) {
//			
//			Lists.newArrayList(256, 1024, 4096, 16000, 64000, 256000, 1000000, 4000000);
//		}
	}
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		final List<Integer> factories = Lists.newArrayList(250, 500, 1000, 4000, 16000, 64000, 256000, 1024000, 4096000);
		final List<Integer> seeds = Lists.newArrayList(74888, 8884, 44784, 48498, 484848, 12458098, 1725551, 8974664, 777);
		final int nbSteps = 10; // number of increments in the number of clients we want
		
		for (int i=0; i<factories.size(); i++) {
			CloudCreator creator = new CloudCreator(nbSteps, seeds.get(i), factories.get(i));
			creator.createTrialGraphs();
		}
		
		
	}
	
	public void createTrialGraphs() {
		float delta = (float) 1 / (float) steps;
		
		for (int i=1; i<=steps; i++) {
			createGraph((int) Math.floor(nbFactories * i * delta));
		}
	}
	
	private void createGraph(int numberOfClients) {
		final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
//		final Workspace workspace = pc.getCurrentWorkspace();
		
		final GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel();
		
		final Graph g = gm.getGraph();
		for (int i=0; i<nbFactories ; i++) {
			if (i%100 == 0) {
				System.out.println("i: " + i);
			}
			Node n = newFactoryNode(gm);
			g.addNode(n);
		}
		
		// 
		// Number of Clients
		//
		for (int i=0; i<numberOfClients; i++) {
			Node n = newClientNode(gm);
			g.addNode(n);
		}
		
		final ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			String graphFilename = "/tmp/ABC" + nbFactories + "f-" + numberOfClients + "c-" + EXPLORATION_TIMEOUT + "et-" + ANT_EXPLORATION_TIMEOUT + "aet-" + ANT_SAMPLING_PROBABILITY + "asp.gexf";
			ec.exportFile(new File(graphFilename));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	
	private Node newFactoryNode(final GraphModel gm) {
		Node n = gm.factory().newNode();
				
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
		
		n.getAttributes().setValue(NodeAttributes.NodeType, NodeAttributeValues.Factory);
		n.getAttributes().setValue(NodeAttributes.ComputationalResourceProfile, NodeAttributeValues.AverageDistributionProcessingTimeProfile);
		n.getAttributes().setValue(NodeAttributes.Seed, seedGen.nextInt(1000, 90000));
		n.getAttributes().setValue(NodeAttributes.Sigma, 0.7);
		n.getAttributes().setValue(NodeAttributes.ProcessingTimePerRequest, String.valueOf(processingTimePerRequest));
		n.getAttributes().setValue(NodeAttributes.ServiceType, serviceType);

		return n;
	}

	private Node newClientNode(final GraphModel gm) {
		Node n = gm.factory().newNode();
		double e = randomWorkflows.nextUniform(0.0, 1.0);
		
		String workflowFactory = NodeAttributeValues.Linear3;
		if (e >= 1.0/3.0 && e < 2.0/3.0) {
			workflowFactory = NodeAttributeValues.Linear2;
		} else if (e >= 2.0/3.0) {
			workflowFactory = NodeAttributeValues.InvertedLinear2;
		}
		
		n.getAttributes().setValue(NodeAttributes.NodeType, NodeAttributeValues.Client);
		n.getAttributes().setValue(NodeAttributes.ExplorationStateTimeout, EXPLORATION_TIMEOUT);
		n.getAttributes().setValue(NodeAttributes.AntExplorationTimeout, ANT_EXPLORATION_TIMEOUT);
		n.getAttributes().setValue(NodeAttributes.AntExplorationSamplingProbability, ANT_SAMPLING_PROBABILITY);
		n.getAttributes().setValue(NodeAttributes.ExplorationBehaviorFactory, NodeAttributeValues.SimpleExplorationBehaviour);
		n.getAttributes().setValue(NodeAttributes.WorkflowFactory, workflowFactory);
		
		return n;
	}

}
