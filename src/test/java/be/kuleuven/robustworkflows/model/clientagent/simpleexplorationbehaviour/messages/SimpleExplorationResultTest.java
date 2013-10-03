package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;

public class SimpleExplorationResultTest {


	@Test
	public void testGetInstance() {
		assertNotNull(SimpleExplorationResult.getInstance(mock(ActorRef.class), 1, ServiceType.A));
	}

	@Test
	public void testIsEmpty() {
		SimpleExplorationResult ser = SimpleExplorationResult.getInstance(mock(ActorRef.class), 1, ServiceType.A);
		assertFalse(ser.isEmpty());
	}

}
