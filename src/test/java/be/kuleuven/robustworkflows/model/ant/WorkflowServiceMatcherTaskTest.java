package be.kuleuven.robustworkflows.model.ant;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.WorkflowServiceMatcherTask;

public class WorkflowServiceMatcherTaskTest {

	@Test(expected=IllegalArgumentException.class)
	public void testGetInstanceNull() {
		WorkflowServiceMatcherTask.getInstance(null);
	}

	@Test
	public void testGetInstance() {
		assertNotNull(WorkflowServiceMatcherTask.getInstance(ServiceType.A));
	}
	
	@Test
	public void setActor() {
		WorkflowServiceMatcherTask task = WorkflowServiceMatcherTask.getInstance(ServiceType.A);
		task.setAgent(mock(ActorRef.class));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void setActorTwice() {
		WorkflowServiceMatcherTask task = WorkflowServiceMatcherTask.getInstance(ServiceType.A);
		task.setAgent(mock(ActorRef.class));
		task.setAgent(mock(ActorRef.class));
		
	}
	
	
	

}
