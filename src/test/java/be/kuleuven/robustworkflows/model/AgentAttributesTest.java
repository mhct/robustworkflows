package be.kuleuven.robustworkflows.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.gephi.graph.api.Attributes;
import org.junit.Before;
import org.junit.Test;

import be.kuleuven.robustworkflows.model.factoryagent.ComputationalResourceProfile;


public class AgentAttributesTest {

	private Attributes nodeAttributes;
	private ComputationalResourceProfile exponentialProfile;

	@Before
	public void setUp() {
		nodeAttributes = mock(Attributes.class);
		when(nodeAttributes.getValue("NodeType")).thenReturn("Factory");
		when(nodeAttributes.getValue("ComputationalResourceProfile")).thenReturn("Exponential");
		when(nodeAttributes.getValue("Seed")).thenReturn("222");
		
		exponentialProfile = ComputationalResourceProfile.exponential(222);
	}
	
	@Test
	public void testGetInstance() {
		assertNotNull(AgentAttributes.getInstance(nodeAttributes, "1"));
	}
	
	@Test
	public void testComputationalProfile() {
		assertEquals(exponentialProfile.expectedTimeToServeRequest(), AgentAttributes.getInstance(nodeAttributes, "1").getComputationalProfile().expectedTimeToServeRequest());
	}

}

