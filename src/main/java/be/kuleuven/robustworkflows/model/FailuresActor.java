package be.kuleuven.robustworkflows.model;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import scala.concurrent.duration.Duration;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.messages.FailureMsg;
import be.kuleuven.robustworkflows.model.messages.StartFailureMsg;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.typesafe.config.Config;

/**
 * Injects Failures into the running system
 * 
 * === Inbound Messages ===
 * - '''Start''' 
 * 
 * === Outbound Messages ===
 * - '''Failure''' Failure message
 * 
 * @author mario
 *
 */
public class FailuresActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	
	private final InfrastructureStorage infrastructureStorage;
	private List<String> actors;
	private RandomDataGenerator random;

	private int failureTime;
	private int concurrentFailure;
	private double failureProbability;
	private long averageFailureDuration;

	private RandomDataGenerator randomDuration;

	
	public FailuresActor(DB db, Config configAttributes) {

		this.infrastructureStorage = new InfrastructureStorage(db);
		actors = Lists.newArrayList();
		random = new RandomDataGenerator(new MersenneTwister());
		randomDuration = new RandomDataGenerator(new MersenneTwister());
		concurrentFailure = configAttributes.getInt("concurrent-failure");
		failureTime = configAttributes.getInt("failure-time");
		failureProbability = configAttributes.getDouble("failure-probability");
		averageFailureDuration = configAttributes.getLong("average-failure-duration");
		
		log.info("Abaddon LOADED. Current time: " + dtf.print(new DateTime()) + toString());
	}

	@Override
	public void onReceive(Object message) throws Exception {
		
		if (StartFailureMsg.class.isInstance(message)) {
			log.info("Abaddon received START msg");
			StartFailureMsg msg = (StartFailureMsg) message;
			
			retrieveActors();
			scheduleFailures(msg.getStartTime());
		}
		else {
			unhandled(message);
		}
	}
	
	private void retrieveActors() {
		DBCursor cursor = null;
		try {
			cursor = infrastructureStorage.getFactoryAgents();
			log.info("Found nbfactories: " + cursor.size());
			while (cursor.hasNext()) {	
				String actorAddress = (String) cursor.next().get("actorAddress");
				actors.add(actorAddress);
			}
		} catch (Exception e) {
			log.error(e.toString());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private void scheduleFailures(long startTime) {
		final int nbAgents = actors.size();
		log.info("nbFactories: " + nbAgents);
		
		for (int i=0; i<nbAgents; i++) {
			double value = random.nextUniform(0.0, 1.0);
			if (value <= failureProbability) {
				DateTime current = new DateTime();
				long scheduledTimeInMillis = current.getMillis() + failureTime + startTime;
				log.info("Scheduling Failure message to=" + actors.get(i) + "," + scheduledTimeInMillis);
				ActorSelection ref = getContext().system().actorSelection(actors.get(i)); //FIXME using deprecated
				scheduleComposeMessage(ref, failureTime+startTime, FailureMsg.getInstance(randomDuration.nextPoisson(averageFailureDuration)));
			}
		}
	}
	
	public void scheduleComposeMessage(final ActorSelection ref, final long time, final FailureMsg failure) {
		getContext().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {

					@Override
					public void run() {
						log.info("Enviando Failure Message" + dtf.print(new DateTime()) + "to :" + ref);
						ref.tell(failure, self());
					}
			
		}, getContext().system().dispatcher());		
	}

	@Override
	public String toString() {
		return "FailuresActor [failureTime=" + failureTime
				+ ", concurrentFailure=" + concurrentFailure
				+ ", failureProbability=" + failureProbability
				+ ", averageFailureDuration=" + averageFailureDuration + "]";
	}

	
}
