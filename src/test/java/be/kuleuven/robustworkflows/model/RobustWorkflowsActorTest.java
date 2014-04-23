//package be.kuleuven.robustworkflows.model;
//
//import static org.junit.Assert.*;
//
//import org.joda.time.format.DateTimeFormat;
//import org.joda.time.format.DateTimeFormatter;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.mongodb.DB;
//
//import akka.actor.Actor;
//import akka.actor.ActorSystem;
//import akka.actor.Props;
//import akka.actor.UntypedActorFactory;
//import akka.testkit.TestActorRef;
//
//public class RobustWorkflowsActorTest {
//	
//	private static ActorSystem system;
//	private DB db;
//
//	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		system = ActorSystem.apply();
//	}
//
//	@AfterClass
//	public static void tearDownAfterClass() throws Exception {
//		system.shutdown();
//	}
//
//	@Test
//	public void testRobustWorkflowsActor() throws InterruptedException {
//		final TestActorRef<RobustWorkflowsActor> wf = TestActorRef.apply(new Props(new UntypedActorFactory() {
//			
//			@Override
//			public Actor create() throws Exception {
//				return new RobustWorkflowsActor(db);
//			}
//		}), system);
//		
//		RobustWorkflowsActor obj = wf.underlyingActor();
//		obj.scheduleComposeMessage("bla", 10000, "0");
//		
//		Thread.sleep(20000);
//	}
//
//	@Test
//	public void testOnReceiveObject() {
//		fail("Not yet implemented");
//	}
//
//}
