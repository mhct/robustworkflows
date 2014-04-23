package be.kuleuven.robustworkflows.model.clientagent;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.RunningCompositionState;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.SimpleExploringState;
import be.kuleuven.robustworkflows.model.messages.ImmutableWorkflowTask;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;

public class RunningCompositionStateTest {

	
	@Test
	public void testRun() {
		ClientAgentProxy cap = mock(ClientAgentProxy.class);
		when(cap.getWorkflow()).thenReturn(Workflow.getInvertedLinear2());
		ArgumentCaptor<WorkflowTask> captor = ArgumentCaptor.forClass(WorkflowTask.class);
		
		ClientAgentState stat = RunningCompositionState.getInstance(cap);
		stat.run();
		stat.run();
//		verify(cap).setState(SimpleExploringState.getInstance(cap, ImmutableWorkflowTask.getInstance(ServiceType.B)));
		
	
	}

	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

}
