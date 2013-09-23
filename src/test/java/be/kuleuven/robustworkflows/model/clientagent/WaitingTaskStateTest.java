package be.kuleuven.robustworkflows.model.clientagent;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mockito;

import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior.WaitingTaskState;
import static org.mockito.Mockito.*;

public class WaitingTaskStateTest {

	@Test
	public void testGetInstance() {
		assertNotNull(WaitingTaskState.getInstance(Mockito.mock(ClientAgentProxy.class)));
	}

	@Test
	public void testOnReceiveCompose() throws Exception {
		ClientAgentProxy mock = mock(ClientAgentProxy.class);
		ModelStorage storage = mock(ModelStorage.class);
		when(mock.getModelStorage()).thenReturn(storage);
		
		ClientAgentState cas = WaitingTaskState.getInstance(mock);
		cas.onReceive("Compose", null);
		
		verify(storage).persistEvent(anyString());
	}

	@Test
	public void testWaitingTaskState() {
//		fail("Not yet implemented");
	}


}
