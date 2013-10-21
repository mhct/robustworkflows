package be.kuleuven.robustworkflows.model.ant;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.TestActorRef;
import akka.testkit.JavaTestKit;

public class SimpleExplorationAntTest {

	private static ActorSystem system;
	private ActorRef ant;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		system = ActorSystem.apply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		system.shutdown();
	}


	@Before
	public void before() {
		ant = system.actorFor("ant0"); 
	}
	
	@Test
	public void testGetInstance() {
		TestActorRef<SimpleExplorationAnt> actorRef = TestActorRef.apply(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() throws Exception {
				return SimpleExplorationAnt.getInstance(new ExplorationAntParameter(null, null, null, 100, 0.2));
			}
		}), "ant0", system);
		
		assertNotNull(actorRef);
		
	}
	
	@Test
	public void timeoutTest() throws Exception {
		
		ant.tell(StartExperimentRun.getInstance("0"), system.deadLetters());
		Future<Object> future = akka.pattern.Patterns.ask(ant, StartExperimentRun.getInstance("0"), 10000);
		assertTrue(future.isCompleted());
		assertEquals(SimpleExplorationResult.getInstance(null, 0, null), Await.result(future, Duration.Zero()));
		Thread.sleep(1000);
	}

}
