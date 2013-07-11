package be.kuleuven.robustworkflows.infrastructure.configuration;

import java.io.File;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

/**
 * Loads a graph from a Gephi file
 * 
 * @author mario
 *
 */
public class GephiGraphImporter {
	
	/**
	 * Loads data from graph file and inserts it on the given workspace
	 * 
	 * @param rawGraph File containing the raw graph data
	 * @param workspace workspace that will receive the graph model which represents the raw graph data
	 */
	private static void insertDataToWorkspace(File rawGraph, Workspace workspace) {
		if (rawGraph == null) {
			System.out.println("there is a problem at the rawGraph");
		} else {
			System.out.println("canReadL: " + rawGraph.canRead());
			System.out.println("abspath: " + rawGraph.getAbsolutePath());
		}
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);        
		Container container;
		try {
		   container = importController.importFile(rawGraph);
		   if(container == null) {
			   System.out.println("Container is null. problem");
		   }
		   container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
		   container.setAllowAutoNode(false);  //Doncreate missing nodes
		} catch (Exception e) {
		   e.printStackTrace();
		   return;
		}
		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);
	}
	
	/**
	 * Gets a graph representation of a graph model
	 * 
	 * @param workspace containing the graph model
	 * @return a DirectedGraph of the model contained in the given workspace
	 */
	private static DirectedGraph getGraph(Workspace workspace) {
		 GraphModel gm = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
		 
		 return gm.getDirectedGraph();
	}
	
	private static Workspace loadWorkspace() { 
		ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();

		return pc.getCurrentWorkspace();
	}

	/**
	 * FactoryMethod, Loads a DirectedGraph from a Gephi file
	 * 
	 * @param gephiFile
	 * @return
	 */
	public static DirectedGraph loadDirectedGraphFrom(final File gephiFile) {
		if (gephiFile == null || !gephiFile.exists()) {
			throw new IllegalArgumentException("File for GRAPH can not be null, empty, or nonexistent.");
		}
		
		try {
			final Workspace workspace = loadWorkspace();
			insertDataToWorkspace(gephiFile, workspace);
			return getGraph(workspace);
			
		} catch (NullPointerException e) {
			throw new IllegalArgumentException("File for GRAPH can not be null or empty");
		}
	}
	
}
