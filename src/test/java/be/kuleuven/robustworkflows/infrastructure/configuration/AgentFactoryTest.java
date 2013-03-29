package be.kuleuven.robustworkflows.infrastructure.configuration;

import static org.junit.Assert.*;

import org.junit.Test;

import be.kuleuven.robustworkflows.model.clientagent.ClientAgent;
import be.kuleuven.robustworkflows.model.factoryagent.FactoryAgent;

public class AgentFactoryTest {


	@Test
	public void testGetInstance() {
		assertNotNull(AgentFactory.getInstance());
	}

	@Test
	public void testHandle() {
		AgentFactory af = AgentFactory.getInstance();
		assertEquals(FactoryAgent.class, af.handle("Factory"));
		assertEquals(ClientAgent.class, af.handle("Client"));
	}
}
