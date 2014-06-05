package be.kuleuven.robustworkflows.util;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import be.kuleuven.robustworkflows.model.antactors.reactive.ReactiveExplorationAntActor;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
import be.kuleuven.robustworkflows.util.StateMachine.StateMachineBuilder;

public class StateMachineTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testStateMachine() {
		StateA idle = new StateA();
		StateB busy = new StateB();
		StateMachineBuilder<Object, Object> builder = StateMachine.create(idle);
		builder.addTransition(idle, String.class, busy);
		builder.addTransition(busy, String.class, idle);
		builder.addTransition(busy, Integer.class, busy);
		
		StateMachine<Object, Object> fsm = builder.build();
		fsm.handle("This is a test", null);
		fsm.handle(12, null);
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
