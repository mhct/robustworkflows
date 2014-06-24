package be.kuleuven.robustworkflows.model.antactors.dmas2;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import be.kuleuven.robustworkflows.model.ant.messages.ExploreService;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.JavaTestKit;
import akka.testkit.TestActorRef;

public class DMAS2ExplorationAntActorTest {

	static ActorSystem system;
	
	@BeforeClass
	public static void setup() {
		system = ActorSystem.create();
	}
	
	@AfterClass
	public static void teardown() {
		JavaTestKit.shutdownActorSystem(system);
		system = null;
	}
	
	@Test
	public void testPostStop() {
		fail("Not yet implemented");
	}

	@Test
	public void testDMAS2ExplorationAntActor() throws Exception {
		ActorRef master = null;
		long timeout = 1800;
		double explorationProbability = 1.0;
		
	    final Props props = Props.create(DMAS2ExplorationAntActor.class, master, timeout, explorationProbability);
	    final TestActorRef<DMAS2ExplorationAntActor> ref = TestActorRef.create(system, props, "DMASExpAnt");
	    final Future<Object> future = akka.pattern.Patterns.ask(ref, Future.class, 3000);
	    
	    assertTrue(future.isCompleted());
	    assertEquals(42, Await.result(future, Duration.Zero()));
	}

	@Test
	public void testOnReceiveObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetExplorationTimeout() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSamplingProbability() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCurrentAgent() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAgentToVisitedNodes() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentAgent() {
		fail("Not yet implemented");
	}

	@Test
	public void testTellMaster() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLoggingAdapter() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetInstance() {
		fail("Not yet implemented");
	}

}
