package be.kuleuven.robustworkflows.model.collect;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class GraphTest {

	@Test
	public void testDFS() {
		Graph<String> graph = Graph.create();
		graph.put("A", "B");
		graph.put("A", "B");

		assertEquals(2, graph.DFS().size());
		Iterator<String> itr = graph.DFS().iterator();
		
		assertEquals("A", itr.next());
		assertEquals("B", itr.next());
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddingNullNodes() {
		Graph<String> graph = Graph.create();
		
		graph.put(null, null);
	}
	
	@Test
	public void testDFSonEmptyGraph() {
		Graph<String> graph = Graph.create();
		
		assertEquals(0, graph.DFS().size());
	}
}
