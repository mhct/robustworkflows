package be.kuleuven.robustworkflows.model.factoryagent;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.ModelStorage;
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

	private static final long TIME_TO_COMPUTE_REQUEST = 500;

	private static final String SERVICE_TYPE = "A";

	private final List<ActorRef> neigbhor;
	private final ModelStorage modelStorage;

	private InfrastructureStorage storage;
//	private ActorRef servicingAgent = null;

//	private ComputationalResourceProfile computationalProfile;
	private final Queue<ReceivedServiceRequest> serviceRequests;

	private boolean busy;


	@Override
	public void preStart() {
		storage.persistFactoryAgentAddress(getSelf().path().name());
	}
	
	
	public FactoryAgent(DB db, List<ActorRef> neighbors, ComputationalResourceProfile computationalProfile) {
		log.info("FactoryAgent started");
		
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.neigbhor = neighbors;
		serviceRequests = new LinkedList<ReceivedServiceRequest>();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if(ActorRef.class.isInstance(message)) {
			//
			// Add reference to current actor
			//
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhor.add((ActorRef) message);
			
		} else if (ServiceRequestExploration.class.isInstance(message)) {
			//TODO consider the service type. perhaps refactor this to the ANT layer... to avoid clutering the code here
			sender().tell(QoSData.getInstance(SERVICE_TYPE, expectedTimeToServeRequest()), self());
			
		} else if (ServiceRequest.class.isInstance(message)) {
			serviceRequests.add(ReceivedServiceRequest.getInstance((ServiceRequest) message, sender()));

		} else if (FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED.equals(message)) {
			ReceivedServiceRequest rsr = serviceRequests.poll();
			if (rsr != null) {
				rsr.actor().tell(ServiceRequestFinished.getInstance(rsr.sr()), self());
				busy = false;
			}
			else {
				log.info("trying to work on empty ServiceRequest list");
			}
			
		} else {
			unhandled(message);
		}
		
		if (!busy) {
			busy = true;
			addExpirationTimer(TIME_TO_COMPUTE_REQUEST, FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED);
		}
	}
	
	private long expectedTimeToServeRequest() {
		return TIME_TO_COMPUTE_REQUEST * serviceRequests.size();
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