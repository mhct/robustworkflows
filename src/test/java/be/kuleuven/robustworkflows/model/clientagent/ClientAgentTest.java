package be.kuleuven.robustworkflows.model.clientagent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.TestActorRef;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * This tests requires a local MONGODB running
 * 
 * @author mario
 *
 */
public class ClientAgentTest {

	private static ActorSystem system;
	private static DB db;
	
	@BeforeClass
	public static void setup() throws UnknownHostException {
		system = ActorSystem.apply();
		db = new MongoClient().getDB("bla");
	}
	
	@AfterClass
	public static void tearDown() {
		system.shutdown();
	}
	
	@Test
	public void testEvaluateComposition() {
		TestActorRef<ClientAgent> clientActor = TestActorRef.create(system, clientAgentProps(), "c1");
		ClientAgent clientAgent = clientActor.underlyingActor();
		
		System.out.println("clientAgent.path() " + clientActor.path().name());
		
		ExplorationResult er1 = mock(ExplorationResult.class);
		when(er1.totalComputationTime()).thenReturn(100l);

		ExplorationResult er2 = mock(ExplorationResult.class);
		when(er2.totalComputationTime()).thenReturn(101l);
		
		ExplorationResult selected = clientAgent.evaluateComposition(Lists.newArrayList(er1, er2));
		
		assertEquals(er1, selected);
		
	}

	public Props clientAgentProps() {
		return Props.apply(new UntypedActorFactory() {
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() throws Exception {
				return new ClientAgent(null, mock(AgentAttributes.class), mock(ExplorationBehaviorFactory.class));
			}
		});		
	}
	//TODO added this algorithm here, only to test if it was ok.. it seems so.. 
//	private ExplorationResult evaluateComposition(List<ExplorationResult> replies) {
		
//		assert replies.get(0) != null;
//		return replies.get(0);

//	@Test
//	public void testAddExpirationTimer() throws InterruptedException {
//		final ActorSystem system = ActorSystem.apply();
//		final DB db = mock(DB.class);
//		final DBCollection coll = mock(DBCollection.class);
//		
//		when(db.getCollection("clientAgents")).thenReturn(coll);
//		
//		final Props props = Props.apply(new UntypedActorFactory() {
//			
//			private static final long serialVersionUID = 1L;
//
//			@Override
//			public Actor create() throws Exception {
//				List<ActorRef> refs = new ArrayList<ActorRef>();
//				return new ClientAgent(db, refs, null);
//			}
//		});
//		
//		final TestActorRef<ClientAgent> client = TestActorRef.create(system, props, "client");
//		client.tell("expirationTimer", system.deadLetters());
//		Thread.sleep(1000);
//	}
//	
	
}
