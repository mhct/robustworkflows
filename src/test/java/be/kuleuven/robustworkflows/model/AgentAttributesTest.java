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
	
	@Test
	public void clientAgentAttributes() {
		Attributes nodeAttributes = mock(Attributes.class);

		long expectedAntExplorationTimeout = 111;
		long expectedExplorationStateTimeout = 1800;
		double expectedAntExplorationSamplingProbability = 1.0;
		
		when(nodeAttributes.getValue(NodeAttributes.NodeType)).thenReturn("Client");
		when(nodeAttributes.getValue(NodeAttributes.ExplorationStateTimeout)).thenReturn(expectedExplorationStateTimeout);
		when(nodeAttributes.getValue(NodeAttributes.AntExplorationTimeout)).thenReturn(expectedAntExplorationTimeout);
		when(nodeAttributes.getValue(NodeAttributes.AntExplorationSamplingProbability)).thenReturn(expectedAntExplorationSamplingProbability);
		
		AgentAttributes att = AgentAttributes.getInstance(nodeAttributes, "1");
		
		assertNotNull(att);
		assertEquals(expectedAntExplorationTimeout, att.getAntExplorationTimeout(), 0.0001);
		assertEquals(expectedExplorationStateTimeout, att.getExplorationStateTimeout(), 0.0001);
		
		assertEquals(expectedAntExplorationSamplingProbability, att.getAntExplorationSamplingProbability(), 0.0001);
	}

}

