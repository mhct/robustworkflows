package be.kuleuven.robustworkflows.model.collect;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

public class GraphTest {

	@Test
	public void testPut() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

	@Test
	public void testHead() {
		fail("Not yet implemented");
	}

	@Test
	public void testRaw() {
		fail("Not yet implemented");
	}

	@Test
	public void testDFS() {
		Graph<String> graph = Graph.create();
		graph.put("A", "B");
		assertEquals(2, graph.DFS().size());
		Iterator<String> itr = graph.DFS().iterator();
		
		assertEquals("A", itr.next());
		assertEquals("B", itr.next());
	}

	@Test
	public void testCreate() {
		fail("Not yet implemented");
	}

}
