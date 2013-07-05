package be.kuleuven.robustworkflows.model.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExplorationReply;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

public class WorkflowServiceMatcherTest {

	@Test
	public void testGetInstance() {
		assertNotNull(WorkflowServiceMatcher.getInstance(Workflow.getLinear1()));
	}
	
	@Test
	public void testSameWorkflowTasks() {
		Workflow expected = Workflow.getLinear(ServiceType.A, ServiceType.B);
		WorkflowServiceMatcher actual = WorkflowServiceMatcher.getInstance(expected);
		
//		TODO Function<F, T> uae this function to transform WorkflowTask -> WorkflowServiceMatcherTask 
		for (WorkflowTask key: expected.rawWorkflow().keys()) {
			Collection<WorkflowTask> expectedTasks = expected.rawWorkflow().get(key);
			Collection<WorkflowServiceMatcherTask> actualTasks = actual.rawWorkflow().get(WorkflowServiceMatcherTask.getInstance(key.getType()));
			
			Iterator<WorkflowTask> itrE = expectedTasks.iterator();
			Iterator<WorkflowServiceMatcherTask> itrA = actualTasks.iterator();
			while (itrE.hasNext() || itrA.hasNext() ) {
				assertEquals(itrE.next().getType(), itrA.next().getType());
			}
		}
	}
	
	@Test
	public void testAssociateAgentB() {
		WorkflowServiceMatcher workflow = WorkflowServiceMatcher.getLinear1();
		
		assertEquals(2, workflow.getNeededServiceTypes().size());
		ServiceRequestExplorationReply mockedReply = mock(ServiceRequestExplorationReply.class);
		ServiceRequestExploration mockedReqExp = mock(ServiceRequestExploration.class);
		when(mockedReqExp.getServiceType()).thenReturn(ServiceType.B);
		when(mockedReply.getRequestExploration()).thenReturn(mockedReqExp);
		
		workflow.associateAgentToTask(mock(ActorRef.class), mockedReply);
		assertEquals(1, workflow.getNeededServiceTypes().size());
		assertEquals(ServiceType.A, workflow.getNeededServiceTypes().iterator().next());
	}

	@Test
	public void testAssociateAgentA() {
		WorkflowServiceMatcher workflow = WorkflowServiceMatcher.getLinear1();
		
		assertEquals(2, workflow.getNeededServiceTypes().size());
		ServiceRequestExplorationReply mockedReply = mock(ServiceRequestExplorationReply.class);
		ServiceRequestExploration mockedReqExp = mock(ServiceRequestExploration.class);
		when(mockedReqExp.getServiceType()).thenReturn(ServiceType.A);
		when(mockedReply.getRequestExploration()).thenReturn(mockedReqExp);
		
		workflow.associateAgentToTask(mock(ActorRef.class), mockedReply);
		assertEquals(1, workflow.getNeededServiceTypes().size());
		assertEquals(ServiceType.B, workflow.getNeededServiceTypes().iterator().next());
	}
	
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
	public void testgetNeededServiceTypes() {
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getLinear1();

		Iterator<ServiceType> itr = wf.getNeededServiceTypes().iterator();
		
		assertNotNull(wf.getNeededServiceTypes());
		assertEquals(2, wf.getNeededServiceTypes().size());
		assertEquals(ServiceType.A, itr.next());
		assertEquals(ServiceType.B, itr.next());
	}

}
