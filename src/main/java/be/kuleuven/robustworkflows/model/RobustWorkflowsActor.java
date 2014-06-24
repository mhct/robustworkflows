package be.kuleuven.robustworkflows.model;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
import be.kuleuven.robustworkflows.model.messages.StartFailureMsg;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.typesafe.config.Config;

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
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	private static final int NUMBER_OF_RUNS = 30;

	private final int startTimeInterval;
	private final int concurrentClients;
//	private static final Long SEED = 898989l;
	
	private int currentRun;
	private final ModelStorage modelStorage;
	private final InfrastructureStorage infrastructureStorage;

	private ActorRef failuresActor;
	
	public RobustWorkflowsActor(DB db, Config configAttributes, ActorRef failuresActor) {
		log.debug("RobustWork Actor. Current time: " + dtf.print(new DateTime()));
		this.modelStorage = ModelStorage.getInstance(db);
		this.infrastructureStorage = new InfrastructureStorage(db);
		currentRun = 0;
		
		startTimeInterval = configAttributes.getInt("start-time-interval");
		concurrentClients = configAttributes.getInt("concurrent-clients");
		this.failuresActor = failuresActor;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if ("Start".equals(message)) {
			startNewExperimentRun();
//		} else if ("CheckExecution".equals(message)) {
//			log.debug("CheckExecution received");
//			if (modelStorage.finishedAllCompositions(String.valueOf(previousRun()))) {
//				log.info("FINISHED ALL COMP");
//				startNewExperimentRun();
//			} else {
//				//do nothing
//			}
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
		log.info("Starting new Experiment RUN");
		log.info("Experiment Stats");
		
		sendStartExperimentRun();
		sendComposeToAllClientAgents();
		
		currentRun++;
	}
	
	private void sendStartExperimentRun() {
		DBCursor cursor = infrastructureStorage.getActors().find();
		
		int i = 0;
		while (cursor.hasNext()) {	
			String actorAddress = (String) cursor.next().get("actorAddress");
			ActorRef ref = getContext().system().actorFor(actorAddress); //FIXME using deprecated
			ref.tell(StartExperimentRun.getInstance(String.valueOf(currentRun)), self());
			
			if (i%1000 == 0) {
				log.info("sent StartExperimentRun msg to more 1000 agents");
			}
			i++;
		}
		
		cursor.close();
	}
	
	private void sendComposeToAllClientAgents() {
		DBCursor cursor = infrastructureStorage.getClientAgents();
		log.info("Retrieving Client Agents: Found: " + cursor.count() + " clients");
		
		long startingTime = startTimeInterval;
		int batch = 0;
		int i = 0;
		while (cursor.hasNext()) {
			
			if (batch % concurrentClients == 0) {
				startingTime += startTimeInterval;
			}

			batch++;
			String ref = (String) cursor.next().get("actorAddress");
			scheduleComposeMessage(ref, startingTime, String.valueOf(currentRun));
			if (i%1000 == 0) {
				log.info("Scheduled ComposeMsg to more 1000 agents");
			}
			i++;
		}
		
		cursor.close();
		
		log.info("Sendind Start to abaddon");
		final StartFailureMsg msg = new StartFailureMsg(startingTime); 
		getContext().system().scheduler().scheduleOnce(Duration.Zero(),
				new Runnable() {
					@Override
					public void run() {
						failuresActor.tell(msg, self());
					}
		}, getContext().system().dispatcher());

	}
	
	public void scheduleComposeMessage(final String ref, final long time, final String run) {
		getContext().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {

					@Override
					public void run() {
						log.info("Enviando Compose Message: " + dtf.print(new DateTime()) + "to :" + ref);
						getContext().system().actorFor(ref).tell(ClientAgentState.COMPOSE, self());
					}
			
		}, getContext().system().dispatcher());		
	}

}
