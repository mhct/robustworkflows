package be.kuleuven.robustworkflows.model.factoryagent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.TestActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class FactoryAgentTest {

	private static ActorSystem system;
	private static DB db;

	@BeforeClass
	public static void setup() throws UnknownHostException {
		system = ActorSystem.apply();
		db = new MongoClient().getDB("bla");
	}

	@AfterClass
	public static void teardown() {
//		JavaTestKit.
	}
	
	@Test
	public void testInvokingFactoryAgentNotReady() throws Exception {

		final TestActorRef<FactoryAgent> factoryRef = TestActorRef.create(system, factoryAgentProps(), "f1");
		final FactoryAgent factoryAgent = factoryRef.underlyingActor();
		
		final ExplorationRequest msg = ExplorationRequest.getInstance(0, ServiceType.A, 0, system.deadLetters(), "kfkf");
		final Future<Object> future = akka.pattern.Patterns.ask(factoryRef, msg, 3000);
		
		assertFalse(future.isCompleted());
		assertNull(factoryAgent.currentRun());
	}

	@Test
	public void testStartingExperiment() throws Exception {
		
		final TestActorRef<FactoryAgent> factoryRef = TestActorRef.create(system, factoryAgentProps(), "f2");
		final FactoryAgent factoryAgent = factoryRef.underlyingActor();
		
		final StartExperimentRun msg0 = StartExperimentRun.getInstance("4");
		final Future<Object> future0 = akka.pattern.Patterns.ask(factoryRef, msg0, 1000);
		assertTrue(future0.isCompleted());
		assertEquals("4", factoryAgent.currentRun());
		
	}

	@Test
	public void testExplorationRequestReply() throws Exception {
		
		final TestActorRef<FactoryAgent> factoryRef = TestActorRef.create(system, factoryAgentProps(), "f3");
		final FactoryAgent factoryAgent = factoryRef.underlyingActor();
		
		final StartExperimentRun msg0 = StartExperimentRun.getInstance("4");
		final Future<Object> future0 = akka.pattern.Patterns.ask(factoryRef, msg0, 1000);
		assertTrue(future0.isCompleted());
		assertEquals("4", factoryAgent.currentRun());
		
		final ExplorationRequest msg1 = ExplorationRequest.getInstance(0, ServiceType.A, 0, system.deadLetters(), "kfkf");
		final Future<Object> future1 = akka.pattern.Patterns.ask(factoryRef, msg1, 3000);
		
		assertEquals(ExplorationReply.getInstance(msg1, 10), Await.result(future1, Duration.Zero()));
	}

	public Props factoryAgentProps() {
		final ComputationalResourceProfile computationalProfile = ComputationalResourceProfile.fixedProcessingTime(10, ServiceType.A);
		
		return Props.apply(new UntypedActorFactory() {
			@Override
			public Actor create() throws Exception {
				return new FactoryAgent(db, null, computationalProfile);
			}
		});		
	}
}
