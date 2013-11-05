package be.kuleuven.robustworkflows.model;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import scala.concurrent.duration.Duration;

import com.typesafe.config.Config;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class SimpleActor  extends UntypedActor {
		private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
		
		private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
		private static final int NUMBER_OF_RUNS = 30;

		private final int startTimeInterval;
		private final int concurrentClients;
//		private static final Long SEED = 898989l;
		
		private int currentRun;
		
		public SimpleActor(Config configAttributes) {
			log.debug("Loading SimpleActor.");
			currentRun = 0;
			
			startTimeInterval = configAttributes.getInt("start-time-interval");
			concurrentClients = configAttributes.getInt("concurrent-clients");

			log.debug("ConcurrentClients: " + concurrentClients);
		}

		@Override
		public void onReceive(Object message) throws Exception {
			
			if ("Start".equals(message)) {
				startNewExperimentRun();
			} else if ("CheckExecution".equals(message)) {
				log.debug("CheckExecution received");
					log.debug("FINISHED ALL COMP");
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
			log.debug("Starting new Experiment RUN");
			
			sendComposeToAllClientAgents();
			
			currentRun++;
		}
		
		private void sendComposeToAllClientAgents() {

			long startingTime = startTimeInterval;
			int batch = 0;
			for (int i=0; i<100; i++) {
				
				if (batch % concurrentClients == 0) {
					startingTime += startTimeInterval;
				}
				batch++;
				log.debug("StartingTime: " + startingTime);
				String ref = "bb";
				scheduleComposeMessage(ref, startingTime, String.valueOf(currentRun));
			}
		}
		
		public void scheduleComposeMessage(final String ref, final long time, final String run) {
			getContext().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
					new Runnable() {

						@Override
						public void run() {
							log.debug("Enviando Compose Message: " + dtf.print(new DateTime()));
						}
				
			}, getContext().system().dispatcher());		
		}

	}
