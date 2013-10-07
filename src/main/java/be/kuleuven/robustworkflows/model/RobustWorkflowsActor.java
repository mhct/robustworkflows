package be.kuleuven.robustworkflows.model;

import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;

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
		this.modelStorage = ModelStorage.getInstance(db);
		this.infrastructureStorage = new InfrastructureStorage(db);
		currentRun = 0;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if ("Start".equals(message)) {
//			sendComposeToAllClientAgents();
			startNewExperimentRun();
		} else if ("CheckExecution".equals(message)) {
			System.out.println("CheckExecution received");
			if (modelStorage.finishedAllCompositions(String.valueOf(previousRun()))) {
				System.out.println("FINISHED ALL COMP");
				startNewExperimentRun();
			} else {
				//do nothing
			}
		} else {
			unhandled(message);
		}
	}
	
	private int previousRun() {
		if (currentRun == 0) {
			return 0;
		} else {
			return currentRun - 1;
		}
	}
	
	private void startNewExperimentRun() {

		if (currentRun == NUMBER_OF_RUNS-1) { 
			return; //end soft execution
		}
		System.out.println("Starting new Experiment RUN");
		
		DBCursor cursor = infrastructureStorage.getActors().find();
		
		while (cursor.hasNext()) {	
			String actorAddress = (String) cursor.next().get("actorAddress");
			ActorRef ref = getContext().system().actorFor(actorAddress);
			ref.tell(StartExperimentRun.getInstance(String.valueOf(currentRun)), self());
//			Future<Object> ret = akka.pattern.Patterns.ask(ref, StartExperimentRun.getInstance(String.valueOf(currentRun)), self(), 1000);
		}
		sendComposeToAllClientAgents();
		
		currentRun++;
	}
	
	private void sendComposeToAllClientAgents() {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(SEED*currentRun));
		
		DBCursor cursor = infrastructureStorage.getClientAgent().find();
		while (cursor.hasNext()) {	
			String ref = (String) cursor.next().get("actorAddress");
			scheduleComposeMessage(getContext().system().actorFor(ref), random.nextPoisson(AVERAGE_ARRIVAL_INTERVAL), String.valueOf(currentRun));
		}
	}
	
	private void scheduleComposeMessage(final ActorRef ref, final long time, final String run) {
		getContext().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.SECONDS), 
				new Runnable() {

					@Override
					public void run() {
						System.out.println("Enviando Compose Message: " + System.currentTimeMillis());
						ref.tell(ClientAgentState.COMPOSE, self());
					}
			
		}, getContext().system().dispatcher());		
	}

}
