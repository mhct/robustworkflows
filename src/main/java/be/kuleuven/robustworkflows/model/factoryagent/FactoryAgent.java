package be.kuleuven.robustworkflows.model.factoryagent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.dmas.AgentType;
import be.kuleuven.dmas.DmasMW;
import be.kuleuven.robustworkflows.model.ModelStorageException;
import be.kuleuven.robustworkflows.model.ModelStorageMap;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;
import be.kuleuven.robustworkflows.model.messages.ExplorationRequest;
import be.kuleuven.robustworkflows.model.messages.FailureMsg;
import be.kuleuven.robustworkflows.model.messages.ReceivedServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequest;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestCompleted;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Provides computational services to any clients
 * version 0.1 doesn't have any capacity constraints, only a particular Average computational time
 * 
 * @author mario
 *
 *  === Inbound messages ===
 * - ''' ActorRef ''' 
 * - ''' StartExperimentRun '''
 * - ''' ExplorationRequest '''
 * - ''' ServiceRequest '''
 * - ''' TIME_TO_WORK_FOR_REQUEST_FINISHED '''
 * - ''' NeihgborListRequest '''
 * 
 * === Outbound messages ===
 * - ''' "OK" '''
 * - ''' ExplorationReply '''
 * - ''' ServiceRequestCompleted '''
 * - ''' Neighbors '''
 * 
 */
public class FactoryAgent extends UntypedActor {

	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private static final String TIME_TO_WORK_FOR_REQUEST_FINISHED = "time_to_service_request";
	
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");

	private ComputationalResourceProfile computationalProfile;

	private boolean busy = false;

	private final DmasMW dmasMW;

	private long previousTime;

	private boolean failure = false;

	private double pheroLevel = 0.0;

	@Override
	public void preStart() {
//		storage.persistFactoryAgentAddress(getName());
//		modelStorage.registerFactoryAgent(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()), computationalProfile.getServiceType());
	}
	
	@Override
	public void postStop() {
//		modelStorage.unRegisterFactoryAgent(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
//		modelStorage.persistWriteCache();
	}
	
	
	public FactoryAgent(List<ActorRef> neighbors, ComputationalResourceProfile computationalProfile) {
		log.info("FactoryAgent started");
		
		this.computationalProfile = computationalProfile; //TODO make a prototype of the computational profile...
		this.previousTime = System.currentTimeMillis();
		
		dmasMW = DmasMW.getInstance(getContext().system(), self(), AgentType.factory());
		dmasMW.addNeighbors(neighbors);

		log.info("neighbors:" + neighbors);
		log.info("computationalProfile:" + computationalProfile);
	}

	/**
	 * Tests if the agent is participating in the execution of an experiment
	 * TODO, this information does not really belong to the model, but to the infrastructure... anyway...
	 * 
	 * @return
	 */
	public boolean isRunningExperiment() {
		return true;
//		if (modelStorage.getField("run") != null) {
//			return true;
//		} else {
//			return false;
//		}
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Message: " + message);
		}
		
		//treats failures
		if (failure) {
			return;
		}
		
		if (FailureMsg.class.isInstance(message)) {
			log.info("Failure message received");
			FailureMsg msg = (FailureMsg) message;
			
			failure = true;
			addExpirationTimer(msg.getDuration(), "RESUME");
		} else if ("RESUME".equals(message)) {
			log.info("Resuming operations");
			failure = false;
			pheroLevel = 0.001;
		}
		
		if (System.currentTimeMillis() - previousTime > 1000) {
			log.info("FactoryAgentQueue=" + (new DateTime()).getMillis() + "," + self().path().name() + "," + computationalProfile.queueSize() );
			previousTime = System.currentTimeMillis();
		}
		// sends the messages to the DmasMW so that it can handle messages etc.
		dmasMW.onReceive(message, sender());
		
		if (StartExperimentRun.class.isInstance(message)) {
			log.debug("Factory received StartExperimentRun: " + ((StartExperimentRun)message).toString());
			
			StartExperimentRun msg = (StartExperimentRun) message;
			if ("".equals(msg.getRun()) || msg.getRun() == null) {
				throw new RuntimeException("StartExperimentRun has no run code");
			}
//			modelStorage.addField("run", msg.getRun());
			computationalProfile.reset();
			sender().tell("OK", self());
		} else if (!isRunningExperiment()) {
			unhandled(message);
		} else if (ExplorationRequest.class.isInstance(message)) {
			log.debug("Factory received ExplorationRequest" + message.toString());
			ExplorationRequest msg = (ExplorationRequest) message;
			//TODO QoSData should be created by computationalProfile, since it has all the needed data to create it.
			if (msg.getServiceType().equals(computationalProfile.getServiceType())) {
				ExplorationRequest sr = (ExplorationRequest) message;
				
//					modelStorage.persistEvent(toDBObject(sr));
				
				sender().tell(ExplorationReply.getInstance(msg, computationalProfile.expectedTimeToServeRequest(), pheroLevel), self());
			} else {
				sender().tell(ExplorationReply.notPossible(msg), self());
			}
			
			
		} else if (ServiceRequest.class.isInstance(message)) {
			log.debug("Factory received ServiceRequest" + message.toString());
			
			ServiceRequest sr = (ServiceRequest) message;
			
			try {
//				modelStorage.persistEvent(toDBObject(sr));
			} catch (ModelStorageException e) {
				//try to insert again
//				modelStorage.persistEvent(e.getObj());
			}
			
			if (sr.typeOf(computationalProfile.getServiceType())) {
				incPheroLevel();
				computationalProfile.add(ReceivedServiceRequest.getInstance((ServiceRequest) message, sender()));
			} else {
				//TODO reply saying this factory does not serve this type of service
			}

		} else if (FactoryAgent.TIME_TO_WORK_FOR_REQUEST_FINISHED.equals(message)) {

			try {
//				modelStorage.persistEvent(FactoryAgentEvents.TIME_TO_WORK_FOR_REQUEST_FINISHED());
			} catch (ModelStorageException e) {
//				modelStorage.persistEvent(e.getObj());
			}
			ReceivedServiceRequest rsr = computationalProfile.poll();
			if (rsr != null) {
				rsr.actor().tell(ServiceRequestCompleted.getInstance(rsr.sr(), getName()), self());
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
	
	private void incPheroLevel() {
		pheroLevel += 0.001;
	}
	
	public Object currentRun() {
//		return modelStorage.getField("run");
		return null;
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

	private DBObject toDBObject(ServiceRequest msg) {
		BasicDBObject obj = new BasicDBObject();
		obj.append(ModelStorageMap.EVENT_TYPE, ServiceRequest.eventType);
		obj.append(ModelStorageMap.CLIENT_AGENT, sender().path().name());
		obj.append(ModelStorageMap.FACTORY_AGENT, self().path().name());
		obj.append(ModelStorageMap.CREATION_TIME, msg.getCreationTime());
		
		return obj;
	}
	private DBObject toDBObject(ExplorationRequest msg) {
		BasicDBObject obj = new BasicDBObject();
		obj.append(ModelStorageMap.EVENT_TYPE, ExplorationRequest.eventType);
		obj.append(ModelStorageMap.CLIENT_AGENT, msg.getOriginName());
		obj.append(ModelStorageMap.FACTORY_AGENT, getSelf().path().name());
		obj.append(ModelStorageMap.EXPLORATION_REQUEST_ID, msg.getId());
		
		return obj;
	}

}