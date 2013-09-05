package be.kuleuven.robustworkflows.infrastructure.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;
import java.util.UUID;

import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.junit.Before;
import org.junit.Test;

import be.kuleuven.robustworkflows.model.NodeAttributes;

public class GephiGraphImporterTester {


	private String graphPath = null;

	private static File loadFileFromPath(String filename) {
		File ret = null;
		try {
			ret = new File(GephiGraphImporterTester.class.getResource(filename).toURI());
		} catch (URISyntaxException e) {
			System.out.println("Shit happens");
			e.printStackTrace();
		}
		System.out.println("Abs: path" + ret.getAbsolutePath());
		return ret;
	}
	
	@Before
	public void setGraphPath() {
		this.graphPath = "1c-1f.gexf";
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void loadDirectedGraphFromNull() {
		assertNotNull(GephiGraphImporter.loadDirectedGraphFrom(null));
	}

	@Test
	public void loadDirectedGraphFrom() {
		File graph = loadFileFromPath(graphPath);
		assertNotNull(GephiGraphImporter.loadDirectedGraphFrom(graph));
	}

	@Test
	public void loadDirectedGraph() {
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath(graphPath));
		
		Node n = graph.getNode("1");
		Attributes a = n.getAttributes();
		a.setValue("ActorRef", "BBBB");
//		for (Node n: graph.getNodes()) {
//			System.out.printf("Node: %s\tType: %s\tLabel: %s\n", n.getNodeData().getId(), n.getAttributes().getValue("NodeType"), n.getAttributes().getValue("Label"));
//			n.getAttributes().setValue("ActorRef", "BLA");
//		}
		
		for (Edge e: graph.getEdges()) {
			System.out.printf("Node: %s -> %s\n", e.getSource().getNodeData().getId(), e.getTarget().getNodeData().getId());
		}
		
		System.out.println("ACtorRef: " + graph.getNode("1").getAttributes().getValue("ActorRef"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void nonExistingFile() {
		GephiGraphImporter.loadDirectedGraphFrom(new File(UUID.randomUUID().toString().concat(".fakefile")));
	}
	
	@Test
	public void checkAttributes() {
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath(graphPath));
		Node n = graph.getNode("1");
		
		assertEquals("440", n.getAttributes().getValue(NodeAttributes.ProcessingTimePerRequest));
		
		assertEquals("B", n.getAttributes().getValue(NodeAttributes.ServiceType));
	}
	
	@Test
	public void checkClientAgentAttributes() {
		//FIXME this is testing the cloudcreator.. in reality 
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath("1c-2f.gexf"));
		Node n = graph.getNode("3");

		assertEquals(1100l, n.getAttributes().getValue(NodeAttributes.ExplorationStateTimeout));
		assertEquals(100l, n.getAttributes().getValue(NodeAttributes.AntExplorationTimeout));
	}

}
