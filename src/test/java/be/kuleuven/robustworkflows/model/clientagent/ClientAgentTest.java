package be.kuleuven.robustworkflows.model.clientagent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.TestActorRef;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class ClientAgentTest {

	@Test
	public void testEvaluateComposition() {
		
		ExplorationResult er1 = mock(ExplorationResult.class);
		when(er1.totalComputationTime()).thenReturn(100l);

		ExplorationResult er2 = mock(ExplorationResult.class);
		when(er2.totalComputationTime()).thenReturn(101l);
		
		ExplorationResult selected = evaluateComposition(Lists.newArrayList(er1, er2));
		
//		assertEquals(er1, selected);
		System.out.println(selected.totalComputationTime());
		
	}

	//TODO added this algorithm here, only to test if it was ok.. it seems so.. 
	private ExplorationResult evaluateComposition(List<ExplorationResult> replies) {
		
		Collections.sort(replies, new Comparator<ExplorationResult>() {

			@Override
			public int compare(ExplorationResult o1, ExplorationResult o2) {
				final long o1Time = o1.totalComputationTime();
				final long o2Time = o2.totalComputationTime();
				
				if (o1Time < o2Time) {
					return -1;
				} else if (o2Time > o2Time) {
					return 1;
				}
					
				return 0;
			}
			
		});
		
		assert replies.get(0) != null;
		return replies.get(0);
	}

	@Test
	public void testAddExpirationTimer() throws InterruptedException {
		final ActorSystem system = ActorSystem.apply();
		final DB db = mock(DB.class);
		final DBCollection coll = mock(DBCollection.class);
		
		when(db.getCollection("clientAgents")).thenReturn(coll);
		
		final Props props = Props.apply(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 1L;

			@Override
			public Actor create() throws Exception {
				List<ActorRef> refs = new ArrayList<ActorRef>();
				return new ClientAgent(db, refs, null);
			}
		});
		
		final TestActorRef<ClientAgent> client = TestActorRef.create(system, props, "client");
		client.tell("expirationTimer", system.deadLetters());
		Thread.sleep(100);
	}
	
	
}
