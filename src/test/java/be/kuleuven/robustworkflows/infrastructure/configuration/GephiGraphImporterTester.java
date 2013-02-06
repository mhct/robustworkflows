package be.kuleuven.robustworkflows.infrastructure.configuration;

import static org.junit.Assert.*;

import java.net.URL;

import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.junit.Before;
import org.junit.Test;

public class GephiGraphImporterTester {


	private String graphPath;

	@Before
	public void getGraphPath() {
//		System.out.println("/" + getClass().getName().replaceAll("\\.", "/") + "/data/simple1.gexf");
//		System.out.println(getClass().getResource("/simple1.gexf").toString());
//		this.graphPath = "/" + getClass().getName().replaceAll("\\.", "/") + "/data/simple1.gexf";
//		System.out.println(graphPath);
		this.graphPath = "/simple1.gexf";
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testLoadDirectedGraphFromNull() {
		assertNotNull(GephiGraphImporter.loadDirectedGraphFrom(null));
	}

	@Test
	public void testLoadDirectedGraphFrom() {
		assertNotNull(GephiGraphImporter.loadDirectedGraphFrom(graphPath));
	}

	@Test
	public void testLoadDirectedGraph() {
		DirectedGraph graph = GephiGraphImporter.loadDirectedGraphFrom(graphPath);
		
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

}
