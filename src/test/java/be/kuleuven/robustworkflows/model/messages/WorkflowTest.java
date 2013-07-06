package be.kuleuven.robustworkflows.model.messages;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.scalatest.selenium.WebBrowser.MultiSel;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.collect.Graph;
//import org.scalatest.matchers.MustMatchers.ArrayMustWrapper;

public class WorkflowTest {

	public static void main(String[] args) {
		Graph<WorkflowTask> tasks = Graph.create();
		
		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.B));
		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.C));
		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.D));
		tasks.put(WorkflowTask.getInstance(ServiceType.B), WorkflowTask.getInstance(ServiceType.C));
		

		//		String head = tasks.keys().iterator().next(); //FIXME check why this returns a different object from different invocations
//		System.out.println("Head" + head);
		
//		AdjacencyList<String> map = AdjacencyList.create();
		Multimap<String, String> map = ArrayListMultimap.create();
		map.put("a", "1");
		map.put("b", "2");
		map.put("c", "3");
		map.put("b", "4");
		for (int i=1; i<100; i++) {
			System.out.println("---->map head: " + map.keys().iterator().next());
			Multiset<String> lista = map.keys();
			for (String a: lista) {
				System.out.println("map list: " + a);
				
			}
			
		}
		
//		map.entries();
//		String head = map.head();
//		for (WorkflowTask w: tasks.get(head)) {
//			System.out.println(w);
//		}
//		assertEquals(4, tasks.entries().size());
//		Workflow w = Workflow.getInstance(tasks);
//		DFS(map, map.head());
//		DFS(tasks, tasks.head());
//		tasks.DFS();
//		map.DFS();
	}
	
	private static <K, V> void  DFS (final Graph<K> graph, K node) {
		System.out.println(node);
		for (K n: graph.get(node)) {
			DFS(graph, n);
		}
	}
	
	@Test
	public void testTotalComputationTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLinear1() {
		fail("Not yet implemented");
	}

	@Test
	public void testRawWorkflow() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLinear() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

}
