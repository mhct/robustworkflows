package be.kuleuven.robustworkflows.model.collect;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ImmutableWorkflowTask;

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

	@Test
	public void testDFS2() {
		Graph<String> graph = Graph.create();
		graph.put("A", "B");
		graph.put("B", "C");
		
		assertEquals(3, graph.DFS().size());
		Iterator<String> itr = graph.DFS().iterator();
		
		assertEquals("A", itr.next());
		assertEquals("B", itr.next());
		assertEquals("C", itr.next());
		
		itr = graph.DFS().iterator();
		while (itr.hasNext()) {
			System.out.println(itr.next());
		}
		
	}
	
	@Test
	public void testDFSReal() {
		Graph<ImmutableWorkflowTask> graph = Graph.create();
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.A),
				ImmutableWorkflowTask.getInstance(ServiceType.B));
		graph.put(ImmutableWorkflowTask.getInstance(ServiceType.B), 
				ImmutableWorkflowTask.getInstance(ServiceType.C));
		
		assertEquals(3, graph.DFS().size());
		Iterator<ImmutableWorkflowTask> itr = graph.DFS().iterator();
		
		itr = graph.DFS().iterator();
		while (itr.hasNext()) {
			System.out.println(itr.next());
		}
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
