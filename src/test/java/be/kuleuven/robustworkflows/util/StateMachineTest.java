package be.kuleuven.robustworkflows.util;

import static org.junit.Assert.*;

import javax.annotation.Nullable;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import be.kuleuven.robustworkflows.util.StateMachine.StateMachineBuilder;

public class StateMachineTest {

	static enum Bla {
		E1, E2;
	};
	
		
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testStateMachine() {
		TestStateRinde tsr = new TestStateRinde();
		StateMachineBuilder<Bla, Object> sm = StateMachine.create(new TestStateRinde());
		sm.addTransition(tsr, Bla.E2, tsr);
	}

	@Test
	public void testHandleC() {
		fail("Not yet implemented");
	}

	@Test
	public void testHandleEC() {
		fail("Not yet implemented");
	}

	@Test
	public void testChangeState() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentState() {
		fail("Not yet implemented");
	}

	@Test
	public void testStateIs() {
		fail("Not yet implemented");
	}

	@Test
	public void testStateIsOneOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStates() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStateOfType() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsSupported() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreate() {
		fail("Not yet implemented");
	}

}