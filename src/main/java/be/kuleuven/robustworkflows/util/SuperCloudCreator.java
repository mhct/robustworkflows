package be.kuleuven.robustworkflows.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.importer.api.Report;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.openide.util.Lookup;

import com.google.common.collect.Lists;

public class SuperCloudCreator {

	/**
	 * Creates a Graph, using the BarabasiAlbert node generator.
	 * 
	 * 
	 * @param nbNodes, number of nodes (in total) to be created
	 * @param percentageClients, the percentage of nodes to represent ClientAgents
	 */
	private void createGraph(int nbNodes, float percentageClients ) {
		
		final BarabasiAlbert generator = new BarabasiAlbert();
		
		generator.setM(2);
		generator.setN(nbNodes);
		generator.setm0(5);

		Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
		container.setReport(new Report());

		generator.setAgentsCreator(new AgentNodesCreator(percentageClients, container.getLoader()));
		
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		
		// Generate the graph
		generator.generate(container.getLoader());
		
		// Imports the data to the current workspace
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
		importController.process(container, new DefaultProcessor(), pc.getCurrentWorkspace());
		
		final ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			String graphFilename = "/tmp/" + nbNodes + "f-" + percentageClients + "c-1.0asp.gexf";
			ec.exportFile(new File(graphFilename), pc.getCurrentWorkspace());
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	
	/**
	 * Main entry point to create Graphs of ClientAgents and FactoryAgents.
	 * The agents, represented by nodes in a graph, are connected to each other forming a ScaleFree network.
	 * This software can be instantiated with or without any command line arguments.
	 * If no command line arguments are given, the application creates 8 graph files, with number of nodes given by the  factories variable.
	 * It accepts two command line parameters,
	 * 
	 * OBS: the graph files are created in the /tmp folder
	 * 
	 * @param args
	 * 	arg[0] = number of nodes to be contained in the graph
	 *  arg[1] = percentage of nodes to be used as ClientAgents
	 *  
	 */
	public static void main(String[] args) {
		float PERCENTAGE_CLIENTS = 0.1f;
		
		final SuperCloudCreator creator = new SuperCloudCreator();

		// Creates a graph based on the command line parameters
		if (args.length >= 1) {
			final int nbNodes = Integer.valueOf(args[0]);
			final float percentageClients = Float.valueOf(args[1]);
			creator.createGraph(nbNodes, percentageClients);
		} else {
			final List<Integer> factories = Lists.newArrayList(250, 1000, 4000, 16000, 64000, 256000, 1024000, 4096000);

			for (int i=0; i<factories.size(); i++) {
				creator.createGraph(factories.get(i), PERCENTAGE_CLIENTS);
			}
		}
	}
}
