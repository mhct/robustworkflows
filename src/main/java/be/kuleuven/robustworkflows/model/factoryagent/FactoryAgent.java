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
import be.kuleuven.robustworkflows.model.messages.QoSData;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;
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

	private final ServiceType SERVICE_TYPE = ServiceType.A; //TODO put this at the ComputationalProfile
	private final List<ActorRef> neigbhor;
	private final ModelStorage modelStorage;

	private InfrastructureStorage storage;
//	private ActorRef servicingAgent = null;

	private ComputationalResourceProfile computationalProfile;
//	private final Queue<ReceivedServiceRequest> serviceRequests;

	private boolean busy = false;


	@Override
	public void preStart() {
		storage.persistFactoryAgentAddress(getSelf().path().name());
	}
	
	
	public FactoryAgent(DB db, List<ActorRef> neighbors, ComputationalResourceProfile computationalProfile) {
		log.info("FactoryAgent started");
		
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.neigbhor = neighbors;
		this.computationalProfile = computationalProfile;
//		serviceRequests = new LinkedList<ReceivedServiceRequest>();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		log.debug("Factory received message " + message);
		
		if(ActorRef.class.isInstance(message)) {
			//
			// Add reference to current actor
			//
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhor.add((ActorRef) message);
			
		} else if (ServiceRequestExploration.class.isInstance(message)) {
			sender().tell(QoSData.getInstance(SERVICE_TYPE, computationalProfile.expectedTimeToServeRequest()), self());
			
		} else if (ServiceRequest.class.isInstance(message)) {
			ServiceRequest sr = (ServiceRequest) message;
			if (sr.typeOf(SERVICE_TYPE)) {
				computationalProfile.add(ReceivedServiceRequest.getInstance((ServiceRequest) message, sender()));
			} else {
				//TODO reply saying this factory does not serve this type of service
			}

		} else if (FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED.equals(message)) {
			ReceivedServiceRequest rsr = computationalProfile.poll();
			if (rsr != null) {
				rsr.actor().tell(ServiceRequestFinished.getInstance(rsr.sr()), self());
			}
			else {
				log.info("trying to work on empty ServiceRequest list");
			}
			busy = false;
			
		} else {
			unhandled(message);
		}
		
		if (!busy && computationalProfile.hasWork()) {
			busy = true;
			addExpirationTimer(computationalProfile.blockFor(), FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED);
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