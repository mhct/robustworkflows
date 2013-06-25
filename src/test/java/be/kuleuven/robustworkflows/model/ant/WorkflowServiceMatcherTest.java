package be.kuleuven.robustworkflows.model.ant;

import static org.junit.Assert.*;

import org.junit.Test;

import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.WorkflowServiceMatcher;

public class WorkflowServiceMatcherTest {

	@Test
	public void testGetLinear1() {
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getLinear1();
		assertNotNull(wf);
	}

	@Test
	public void testGetLinear() {
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getLinear(ServiceType.A);
		assertNotNull(wf);
	}

	@Test
	public void testGetLinearMultipleServices() {
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getLinear(ServiceType.A, ServiceType.B, ServiceType.C);
		
		assertNotNull(wf);
	}
	
	@Test
	public void testgetNeededTasks() {
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getLinear1();

		assertNotNull(wf.getNeededServiceTypes());
		assertNotSame(0, wf.getNeededServiceTypes().size());
		assertEquals(ServiceType.A, wf.getNeededServiceTypes().iterator().next());
		assertEquals(ServiceType.B, wf.getNeededServiceTypes().iterator().next());
	}

}
