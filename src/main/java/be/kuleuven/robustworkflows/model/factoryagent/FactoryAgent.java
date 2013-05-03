package be.kuleuven.robustworkflows.model.factoryagent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.messages.QoSData;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;

import com.mongodb.DB;

/**
 * Provides computational services to any clients
 * version 0.1 doesn't have any capacity constraints, only a particular Average computational time
 * @author mario
 *
 */
public class FactoryAgent extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private static final String TIME_TO_WORK_FOR_REQUEST_FINISHED = "time_to_service_request";

	private final List<ActorRef> neigbhor;
	private final ModelStorage modelStorage;

	private InfrastructureStorage storage;
	private ActorRef servicingAgent = null;

	private ComputationalResourceProfile computationalProfile;


	@Override
	public void preStart() {
		storage.persistFactoryAgentAddress(getSelf().path().name());
	}
	
//	public FactoryAgent(DB db, List<ActorRef> neighbors) {
//		log.info("FactoryActor started");
//		
//		this.storage = new InfrastructureStorage(db);
//		this.modelStorage = new ModelStorage(db);
//		this.neigbhor = neighbors;
//		this.random = new RandomDataGenerator(new MersenneTwister(SEED_SORCERER_SELECTION));
//	}
	
	public FactoryAgent(DB db, List<ActorRef> neighbors, ComputationalResourceProfile computationalProfile) {
		log.info("FactoryAgent started");
		
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.neigbhor = neighbors;
		this.computationalProfile = computationalProfile;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhor.add((ActorRef) message);
		} else if (ServiceRequestExploration.class.isInstance(message)) {
			sender().tell(QoSData.getInstance("A", computationalProfile.expectedTime(100)), self());
//			sender().tell(QoSData.getInstance(random.nextPoisson(avgComputationTime)), self());
		} else if (ServiceRequest.class.isInstance(message)) {
			modelStorage.persistEvent("FactoryAgent: " + self().path().name() + ", Engaging in Composition");
			servicingAgent = sender();
//			addExpirationTimer(random.nextLong((long)(avgComputationTime - 300), (long)(avgComputationTime + 300)), FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED);

			//TODO add a place to count the number of requests per second, etc..
			addExpirationTimer(computationalProfile.expectedTime(100), FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED);
		} else if (FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED.equals(message)) {
			servicingAgent.tell(ServiceRequestFinished.getInstance(), self());
		} else {
			unhandled(message);
		}
		
	}
	
	private void addExpirationTimer(long time, final String message) {
		context().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, null);
					}
		}, context().system().dispatcher());
	}



}