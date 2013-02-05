package be.kuleuven.robustworkflows.infrastructure;

import java.io.File;
import java.net.URISyntaxException;

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
	
	public static void insertDataToWorkspace(File rawGraph, Workspace workspace) {
		
		ImportController importController = Lookup.getDefault().lookup(ImportController.class);        
		Container container;
		try {
		   container = importController.importFile(rawGraph);
		   container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
		   container.setAllowAutoNode(false);  //Doncreate missing nodes
		} catch (Exception ex) {
		   ex.printStackTrace();
		   return;
		}
		//Append imported data to GraphAPI
		importController.process(container, new DefaultProcessor(), workspace);
	}
	
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
	 * Loads a DirectedGraph from a Gephi file
	 * 
	 * @param gephiFile
	 * @return
	 */
	public static DirectedGraph loadDirectedGraphFrom(String gephiFile) {
		try {
			final File rawGraph = new File(GephiGraphImporter.class.getResource(gephiFile).toURI());
			final Workspace workspace = loadWorkspace();
			
			insertDataToWorkspace(rawGraph, workspace);
			
			return getGraph(workspace);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException("File path for GRAPH can not be null or empty");
		}
	}
	
	//	public void iterate() {
//		 for(Node n: graph.getNodes()) {
//			 System.out.println("node: " + n.getId());
//		 }
//		 
//		 for(Edge e: graph.getEdges()) {
//			 System.out.println("e: " +e.getId() + " :  "+ e.getSource().getId() + " -> " + e.getTarget().getId());
//		 }
//	}
}
