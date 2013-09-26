package be.kuleuven.robustworkflows.model;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import scala.concurrent.duration.Duration;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.messages.Compose;

import com.mongodb.DB;
import com.mongodb.DBCursor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

/**
 * Loads experiments on the network...
 * 
 * === Inbound Messages ===
 * - '''CheckExecution''' Checks if it is time to start a new RUN
 * 
 * === Outbound Messages ===
 * - '''Compose''' Message to ClientAgents to start e new composition
 * 
 * 
 * @author mario
 *
 */
public class RobustWorkflowsActor extends UntypedActor {

	private static final int AVERAGE_ARRIVAL_INTERVAL = 10;
	private static final int NUMBER_OF_RUNS = 30;
	private static final Long SEED = 898989l;
	
	private final ModelStorage modelStorage;
	private final InfrastructureStorage infrastructureStorage;
	private int currentRun;
	
	public RobustWorkflowsActor(DB db) {
		this.modelStorage = new ModelStorage(db);
		this.infrastructureStorage = new InfrastructureStorage(db);
		currentRun = 0;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if ("Start".equals(message)) {
			sendComposeToAllClientAgents();
		} else if ("CheckExecution".equals(message)) {
			System.out.println("CheckExecution received");
			if (modelStorage.finishedAllCompositions(String.valueOf(currentRun-1))) {
				System.out.println("FINISHED ALL COMP.. starting new run");
				sendComposeToAllClientAgents();
			} else {
				//do nothing
			}
		} else {
			unhandled(message);
		}
	}
	
	private void sendComposeToAllClientAgents() {
//		log.debug("Searching Clients to send Compose Message");
//		if (true) {
//			DBObject obj = storage.getClientAgent("389");
//			if (obj == null) {
//				System.out.println("Could not find ClientAgent");
//			} else {
//				sendComposeMessage(system.actorFor((String) obj.get("actorAddress")), 1);
//			}
//			return;
//		}
		String ref = "";

		if (currentRun <= NUMBER_OF_RUNS) {
			RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(SEED*currentRun));
			
			DBCursor cursor = infrastructureStorage.getClientAgent().find();
			while (cursor.hasNext()) {	
				ref = (String) cursor.next().get("actorAddress");
				sendComposeMessage(getContext().system().actorFor(ref), random.nextPoisson(AVERAGE_ARRIVAL_INTERVAL), String.valueOf(currentRun));
			}
			currentRun++;
		}
	}
	
	private void sendComposeMessage(final ActorRef ref, final long time, final String run) {
//		log.debug("Sending ComposeMessage to be executed in : " + time + " s");
		getContext().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.SECONDS), 
				new Runnable() {

					@Override
					public void run() {
						System.out.println("Enviando Compose Message: " + System.currentTimeMillis());
						ref.tell(Compose.getInstance(run), self());
					}
			
		}, getContext().system().dispatcher());		
	}

}
