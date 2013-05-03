package be.kuleuven.robustworkflows.infrastructure.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import akka.actor.Actor;
import akka.actor.ActorInitializationException;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgent;
import be.kuleuven.robustworkflows.model.factoryagent.FactoryAgent;

import com.mongodb.DB;

public class AgentFactoryTest {


	@Test
	public void testGetInstance() {
		assertNotNull(AgentFactory.getInstance());
	}

	@Test
	public void testHandle() {
		AgentFactory af = AgentFactory.getInstance();
		
		AgentAttributes attributes = mock(AgentAttributes.class);
		when(attributes.getAgentType()).thenReturn("Factory");
		assertEquals(FactoryAgent.class, af.handle(attributes));

		when(attributes.getAgentType()).thenReturn("Client");
		assertEquals(ClientAgent.class, af.handle(attributes));
	}
	
	@Test(expected=ActorInitializationException.class)
	public void loadFactoryAgent() {
		AgentFactory af = AgentFactory.getInstance();
		AgentAttributes attributes = mock(AgentAttributes.class);
		when(attributes.getAgentType()).thenReturn("Factory");
		
		af.handleInstance(attributes, mock(DB.class));
	}

	@Test(expected=ActorInitializationException.class)
	public void loadClientAgent() {
		AgentFactory af = AgentFactory.getInstance();
		AgentAttributes attributes = mock(AgentAttributes.class);
		when(attributes.getAgentType()).thenReturn("Client");
		
		af.handleInstance(attributes, mock(DB.class));
	}
}
