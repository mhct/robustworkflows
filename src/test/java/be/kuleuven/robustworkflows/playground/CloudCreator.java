package be.kuleuven.robustworkflows.playground;

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
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import be.kuleuven.robustworkflows.model.NodeAttributeValues;
import be.kuleuven.robustworkflows.model.NodeAttributes;
import be.kuleuven.robustworkflows.model.ServiceType;

import com.google.common.collect.Lists;

public class CloudCreator {

	private static int SEED = 8080823;
	private static RandomDataGenerator random1 = new RandomDataGenerator(new MersenneTwister(SEED));
	private static RandomDataGenerator random2 = new RandomDataGenerator(new MersenneTwister(SEED));
	private static RandomDataGenerator randomWorkflows = new RandomDataGenerator(new MersenneTwister(SEED));
	private static RandomDataGenerator seedGen = new RandomDataGenerator(new MersenneTwister(SEED));

	private static final int NUMBER_OF_FACTORIES = 15;
	private static final int NUMBER_OF_CLIENTS = 100;
	private static final long EXPLORATION_TIMEOUT = 2050;
	private static final long ANT_EXPLORATION_TIMEOUT = 1950;
	private static  double ANT_SAMPLING_PROBABILITY;
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
//		List<Double> probs = Lists.newArrayList(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0);
		List<Double> probs = Lists.newArrayList(1.0);
		
		for (Double p: probs) {
			ANT_SAMPLING_PROBABILITY = p;
			createGraph();
		}
	}
	
	private static void createGraph() {
		final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		final Workspace workspace = pc.getCurrentWorkspace();
		
		final GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel();
		
		final Graph g = gm.getGraph();
		for (int i=0; i<NUMBER_OF_FACTORIES ; i++) {
			if (i%100 == 0) {
				System.out.println("i: " + i);
			}
			Node n = newFactoryNode(gm);
			g.addNode(n);
		}
		
		for (int i=0; i<NUMBER_OF_CLIENTS; i++) {
			Node n = newClientNode(gm);
			g.addNode(n);
		}
		
		final ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			String graphFilename = "/tmp/ABC" + NUMBER_OF_CLIENTS + "c-" + NUMBER_OF_FACTORIES + "f-" + EXPLORATION_TIMEOUT + "et-" + ANT_EXPLORATION_TIMEOUT + "aet-" + ANT_SAMPLING_PROBABILITY + "asp.gexf";
			ec.exportFile(new File(graphFilename));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	
	private static StringBuilder factoriesToCSV(Graph graph) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("id,ServiceType,time\n");
		for (Node n: graph.getNodes()) {
			sb.append(n.getId());
			sb.append(",");
			sb.append(n.getAttributes().getValue(NodeAttributes.ServiceType));
			sb.append(",");
			sb.append(n.getAttributes().getValue(NodeAttributes.ProcessingTimePerRequest));
		}
		
		return sb;
	}
	
	private static Node newFactoryNode(final GraphModel gm) {
		Node n = gm.factory().newNode();
				
		final int processingTimePerRequest = random1.nextInt(10000, 30000);
		double e = random2.nextUniform(0, 1);

		String serviceType = ServiceType.A.toString();
		if (e >= 1.0/3.0 && e < 2.0/3.0) {
			serviceType = ServiceType.B.toString();
		} else if (e >= 2.0/3.0) {
			serviceType = ServiceType.C.toString();
		}
		
		n.getAttributes().setValue(NodeAttributes.NodeType, NodeAttributeValues.Factory);
		n.getAttributes().setValue(NodeAttributes.ComputationalResourceProfile, NodeAttributeValues.AverageDistributionProcessingTimeProfile);
		n.getAttributes().setValue(NodeAttributes.Seed, seedGen.nextInt(1000, 90000));
		n.getAttributes().setValue(NodeAttributes.Sigma, 0.7);
		n.getAttributes().setValue(NodeAttributes.ProcessingTimePerRequest, String.valueOf(processingTimePerRequest));
		n.getAttributes().setValue(NodeAttributes.ServiceType, serviceType);

		return n;
	}

	private static Node newClientNode(final GraphModel gm) {
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
