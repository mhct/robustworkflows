package be.kuleuven.robustworkflows.model.messages;

import static org.junit.Assert.*;

import org.junit.Test;
//import org.scalatest.matchers.MustMatchers.ArrayMustWrapper;

import be.kuleuven.robustworkflows.model.ServiceType;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class WorkflowTest {

	public static void main(String[] args) {
		Multimap<WorkflowTask, WorkflowTask> tasks = ArrayListMultimap.create();
		
		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.B));
		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.C));
		tasks.put(WorkflowTask.getInstance(ServiceType.A), WorkflowTask.getInstance(ServiceType.D));
		tasks.put(WorkflowTask.getInstance(ServiceType.B), WorkflowTask.getInstance(ServiceType.C));
		
		WorkflowTask head = tasks.keys().iterator().next();
		System.out.println("Head" + head);
//		for (WorkflowTask w: tasks.get(head)) {
//			System.out.println(w);
//		}
		assertEquals(4, tasks.entries().size());
//		Workflow w = Workflow.getInstance(tasks);
		DFS(tasks, head);
	}
	
	private static void DFS(final Multimap<WorkflowTask, WorkflowTask> graph, WorkflowTask node) {
		System.out.println(node);
		for (WorkflowTask n: graph.get(node)) {
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
