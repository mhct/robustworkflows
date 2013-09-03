package be.kuleuven.robustworkflows.playground;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

public class CloudCreator {

	private static int SEED = 808080;
	private static RandomDataGenerator random1 = new RandomDataGenerator(new MersenneTwister(SEED));
	private static RandomDataGenerator random2 = new RandomDataGenerator(new MersenneTwister(SEED));

	private static final int NUMBER_OF_FACTORIES = 100;
	private static final int NUMBER_OF_CLIENTS = 1000;
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
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
			String graphFilename = "/tmp/" + NUMBER_OF_CLIENTS + "c-" + NUMBER_OF_FACTORIES + "f-long-times.gexf";
			ec.exportFile(new File(graphFilename));
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	
	private static StringBuilder factoriesToCSV(Graph graph) {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("id,ServiceType,time");
		for (Node n: graph.getNodes()) {
			sb.append(n.getId());
			sb.append(",");
			sb.append(n.getAttributes().getValue("ServiceType"));
			sb.append(",");
			sb.append(n.getAttributes().getValue("ProcessingTimePerRequest"));
		}
		
		return sb;
	}
	
	private static Node newFactoryNode(final GraphModel gm) {
		Node n = gm.factory().newNode();
				
		final int processingTimePerRequest = random1.nextInt(10000, 60000);
		String serviceType = "A";
		double e;
		if ((e = random2.nextUniform(0, 1)) >= 0.5) {
			serviceType = "B";
		}
		n.getAttributes().setValue("NodeType", "Factory");
		n.getAttributes().setValue("ComputationalResourceProfile", "FixedProcessingTime");
		n.getAttributes().setValue("ProcessingTimePerRequest", String.valueOf(processingTimePerRequest));
		n.getAttributes().setValue("ServiceType", serviceType);

		return n;
	}

	private static Node newClientNode(final GraphModel gm) {
		Node n = gm.factory().newNode();
		
		n.getAttributes().setValue("NodeType", "Client");
		
		return n;
	}

}
