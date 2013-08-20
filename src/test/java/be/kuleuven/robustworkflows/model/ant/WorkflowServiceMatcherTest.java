package be.kuleuven.robustworkflows.model.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

public class WorkflowServiceMatcherTest {

	@Test
	public void testGetInstance() {
		assertNotNull(WorkflowServiceMatcher.getInstance(Workflow.getLinear1()));
	}
	
//	@Test
//	public void testSameWorkflowTasks() {
//		Workflow expected = Workflow.getLinear1();
//		WorkflowServiceMatcher actual = WorkflowServiceMatcher.getInstance(expected);
//		
////		TODO Function<F, T> uae this function to transform WorkflowTask -> WorkflowServiceMatcherTask 
//		for (WorkflowTask key: expected.rawWorkflow().keys()) {
//			Collection<WorkflowTask> expectedTasks = expected.rawWorkflow().get(key);
//			Collection<MutableWorkflowTask> actualTasks = actual.rawWorkflow().get(MutableWorkflowTask.getInstance(key.getType()));
//			
//			Iterator<WorkflowTask> itrE = expectedTasks.iterator();
//			Iterator<MutableWorkflowTask> itrA = actualTasks.iterator();
//			
//			while (itrE.hasNext() || itrA.hasNext() ) {
//				assertEquals(itrE.next().getType(), itrA.next().getType());
//			}
//		}
//	}
	
	@Test
	public void testAssociateAgentB() {
		WorkflowServiceMatcher workflow = WorkflowServiceMatcher.getInstance(Workflow.getLinear1());
		
		assertEquals(2, workflow.getNeededServiceTypes().size());
		ExplorationReply mockedReply = mock(ExplorationReply.class);
		ExplorationRequest mockedReqExp = mock(ExplorationRequest.class);
		when(mockedReqExp.getServiceType()).thenReturn(ServiceType.B);
		when(mockedReply.getRequestExploration()).thenReturn(mockedReqExp);
		
		workflow.addReply(mock(ActorRef.class), mockedReply);
		assertEquals(1, workflow.getNeededServiceTypes().size());
		assertEquals(ServiceType.A, workflow.getNeededServiceTypes().iterator().next());
	}

	@Test
	public void testAssociateAgentA() {
		WorkflowServiceMatcher workflow = WorkflowServiceMatcher.getInstance(Workflow.getLinear1());
		
		assertEquals(2, workflow.getNeededServiceTypes().size());
		ExplorationReply mockedReply = mock(ExplorationReply.class);
		ExplorationRequest mockedReqExp = mock(ExplorationRequest.class);
		when(mockedReqExp.getServiceType()).thenReturn(ServiceType.A);
		when(mockedReply.getRequestExploration()).thenReturn(mockedReqExp);
		
		workflow.addReply(mock(ActorRef.class), mockedReply);
		assertEquals(1, workflow.getNeededServiceTypes().size());
		assertEquals(ServiceType.B, workflow.getNeededServiceTypes().iterator().next());
	}
	
	@Test
	public void testgetNeededServiceTypes() {
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getInstance(Workflow.getLinear1());

		Iterator<ServiceType> itr = wf.getNeededServiceTypes().iterator();
		
		assertNotNull(wf.getNeededServiceTypes());
		assertEquals(2, wf.getNeededServiceTypes().size());
		assertEquals(ServiceType.A, itr.next());
		assertEquals(ServiceType.B, itr.next());
	}
	
	
	@Test
	public void testAddReply_createOptimalWorkflow() {
		final ActorSystem system = ActorSystem.apply();
		final Props props = Props.apply(MyActor.class);
		final TestActorRef<MyActor> ac0 = TestActorRef.create(system, props, "ac0");
		final TestActorRef<MyActor> ac1 = TestActorRef.create(system, props, "ac1");
		final TestActorRef<MyActor> ac2 = TestActorRef.create(system, props, "ac2");
		WorkflowServiceMatcher wf = WorkflowServiceMatcher.getInstance(Workflow.getLinear1());
		
		ExplorationRequest er0 = ExplorationRequest.getInstance(0, ServiceType.A, 10, ac0);
		ExplorationRequest er1 = ExplorationRequest.getInstance(0, ServiceType.B, 10, ac1);

		ExplorationReply res0 = ExplorationReply.getInstance(er0, 1);
		ExplorationReply res1 = ExplorationReply.getInstance(er1, 3);
		ExplorationReply res2 = ExplorationReply.getInstance(er1, 10);
		
		wf.addReply(ac0, res0);
		wf.addReply(ac1, res1);
		wf.addReply(ac2, res2); //takes more time, ac2 shouldn't be selected
		
		//Test starts here
		final Workflow pairedWorkflow = wf.createOptimalWorkflow();
		final Iterator<WorkflowTask> itr = pairedWorkflow.iterator();
		
		assertEquals(ac0, itr.next().getAgent());
		assertEquals(ac1, itr.next().getAgent());
	}

}


class MyActor extends UntypedActor {

	@Override
	public void onReceive(Object arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}