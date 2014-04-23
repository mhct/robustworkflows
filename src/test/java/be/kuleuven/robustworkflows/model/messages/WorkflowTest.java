package be.kuleuven.robustworkflows.model.messages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Iterator;

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

	@Test
	public void instance() {
		Workflow wf = Workflow.getLinear3();
		Iterator<WorkflowTask> itr = wf.iterator();
		
		while (itr.hasNext()) {
			System.out.println(itr.next());
		}
	}
	
	@Test
	public void testInvertedLinear2Iterator() {
		Workflow wf = Workflow.getInvertedLinear2();
		Iterator<WorkflowTask> itr = wf.iterator();
		
		WorkflowTask t = itr.next();
		assertEquals(ImmutableWorkflowTask.getInstance(ServiceType.B), t);
		t = itr.next();
		assertEquals(ImmutableWorkflowTask.getInstance(ServiceType.A), t);
		
		assertFalse(itr.hasNext());
	}

	@Test
	public void testLinear2Iterator() {
		Workflow wf = Workflow.getLinear2();
		Iterator<WorkflowTask> itr = wf.iterator();
		
		WorkflowTask t = itr.next();
		assertEquals(ImmutableWorkflowTask.getInstance(ServiceType.A), t);
		t = itr.next();
		assertEquals(ImmutableWorkflowTask.getInstance(ServiceType.B), t);
		
		assertFalse(itr.hasNext());
	}
//	public static void main(String[] args) {
//		Graph<WorkflowTask> tasks = Graph.create();
//		
//		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.B));
//		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.C));
//		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.D));
//		tasks.put(WorkflowTask.getInstance(ServiceType.B), WorkflowTask.getInstance(ServiceType.C));
//		
//
//		//		String head = tasks.keys().iterator().next(); //FIXME check why this returns a different object from different invocations
////		System.out.println("Head" + head);
//		
////		AdjacencyList<String> map = AdjacencyList.create();
//		Multimap<String, String> map = ArrayListMultimap.create();
//		map.put("a", "1");
//		map.put("b", "2");
//		map.put("c", "3");
//		map.put("b", "4");
//		for (int i=1; i<100; i++) {
//			System.out.println("---->map head: " + map.keys().iterator().next());
//			Multiset<String> lista = map.keys();
//			for (String a: lista) {
//				System.out.println("map list: " + a);
//				
//			}
//			
//		}
//		
////		map.entries();
////		String head = map.head();
////		for (WorkflowTask w: tasks.get(head)) {
////			System.out.println(w);
////		}
////		assertEquals(4, tasks.entries().size());
////		Workflow w = Workflow.getInstance(tasks);
////		DFS(map, map.head());
////		DFS(tasks, tasks.head());
////		tasks.DFS();
////		map.DFS();
//	}
	

}
