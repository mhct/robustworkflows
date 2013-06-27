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
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestFinished;

import com.google.common.collect.Lists;
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

	private final List<ActorRef> neigbhors;
	private final ModelStorage modelStorage;
	private InfrastructureStorage storage;
	private ComputationalResourceProfile computationalProfile;

	private boolean busy = false;


	@Override
	public void preStart() {
		storage.persistFactoryAgentAddress(getName());
		modelStorage.registerFactoryAgent(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()), computationalProfile.getServiceType());
	}
	
	@Override
	public void postStop() {
		modelStorage.unRegisterFactoryAgent(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
	}
	
	public FactoryAgent(DB db, List<ActorRef> neighbors, ComputationalResourceProfile computationalProfile) {
		log.info("FactoryAgent started");
		
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.neigbhors = neighbors;
		this.computationalProfile = computationalProfile;
		
	}

	
	@Override
	public void onReceive(Object message) throws Exception {
		log.debug("Factory received message " + message);
		
		if(ActorRef.class.isInstance(message)) {
			//
			// Add reference to current actor
			//
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhors.add((ActorRef) message);
			
		} else if (ServiceRequestExploration.class.isInstance(message)) {
			//TODO QoSData should be created by computationalProfile, since it has all the needed data to create it.
			sender().tell(ServiceRequestExplorationReply.getInstance(computationalProfile.getServiceType(), computationalProfile.expectedTimeToServeRequest()), self());
			
			
		} else if (ServiceRequest.class.isInstance(message)) {
			ServiceRequest sr = (ServiceRequest) message;
			if (sr.typeOf(computationalProfile.getServiceType())) {
				computationalProfile.add(ReceivedServiceRequest.getInstance((ServiceRequest) message, sender()));
			} else {
				//TODO reply saying this factory does not serve this type of service
			}

		} else if (FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED.equals(message)) {
			ReceivedServiceRequest rsr = computationalProfile.poll();
			if (rsr != null) {
				rsr.actor().tell(ServiceRequestFinished.getInstance(rsr.sr(), getName()), self());
			}
			else {
				log.info("trying to work on empty ServiceRequest list");
			}
			busy = false;
			
		} else if (EventType.NeihgborListRequest.toString().equals(message)){
			sender().tell(getNeighbors(), self());
		} else {
			unhandled(message);
		}
		
		if (!busy && computationalProfile.hasWork()) {
			busy = true;
			addExpirationTimer(computationalProfile.blockFor(), FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED);
		}
	}
	
	private Neighbors getNeighbors() {
		return Neighbors.getInstance(neigbhors);
	}
	
	private String getName() {
		return getSelf().path().name();
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