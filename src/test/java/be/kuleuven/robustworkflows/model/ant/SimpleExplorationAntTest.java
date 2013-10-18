package be.kuleuven.robustworkflows.model.ant;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.TestActorRef;

public class SimpleExplorationAntTest {

	private static ActorSystem system;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		system = ActorSystem.apply();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		system.shutdown();
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

}
