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

public class GephiGraphImporterTester {


	private String graphPath;

	private static File loadFileFromPath(String filename) {
		File ret = null;
		try {
			ret = new File(GephiGraphImporterTester.class.getResource(filename).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println("Abs: path" + ret.getAbsolutePath());
		return ret;
	}
	
	@Before
	public void setGraphPath() {
		this.graphPath = "/simple1.gexf";
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLoadDirectedGraphFromNull() {
		assertNotNull(GephiGraphImporter.loadDirectedGraphFrom(null));
	}

	@Test
	public void testLoadDirectedGraphFrom() {
		assertNotNull(GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath(graphPath)));
	}

	@Test
	public void testLoadDirectedGraph() {
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath(graphPath));
		
		Node n = graph.getNode("60");
		Attributes a = n.getAttributes();
		a.setValue("ActorRef", "BBBB");
//		for (Node n: graph.getNodes()) {
//			System.out.printf("Node: %s\tType: %s\tLabel: %s\n", n.getNodeData().getId(), n.getAttributes().getValue("NodeType"), n.getAttributes().getValue("Label"));
//			n.getAttributes().setValue("ActorRef", "BLA");
//		}
		
		for (Edge e: graph.getEdges()) {
			System.out.printf("Node: %s -> %s\n", e.getSource().getNodeData().getId(), e.getTarget().getNodeData().getId());
		}
		
		System.out.println("ACtorRef: " + graph.getNode(6).getAttributes().getValue("ActorRef"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNonExistingFile() {
		GephiGraphImporter.loadDirectedGraphFrom(new File(UUID.randomUUID().toString().concat(".fakefile")));
	}
	
	@Test
	public void testAttributes() {
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath(graphPath));
		Node n = graph.getNode("60");
		
		assertEquals("123", n.getAttributes().getValue("Seed"));
		assertEquals("Exponential", n.getAttributes().getValue("ComputationalResourceProfile"));
	}
	
//	@Test
	public static void main(String[] args) {
//		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath("/scenario1/internet_routers-22july06.gml"));
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(loadFileFromPath("/simple1.gexf"));
		System.out.println(graph.getNodeCount());
	}

}
