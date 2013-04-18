package be.kuleuven.robustworkflows.model.clientagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;


import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ant.AntAPI;
import be.kuleuven.robustworkflows.model.messages.QoSData;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;

import com.mongodb.DB;

/**
 * 
 * @author mario
 *
 */
public class ClientAgent extends UntypedActor implements ClientAgentProxy {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final List<ActorRef> neighbors;
	private ClientAgentState currentState;
	private InfrastructureStorage storage;
	private ModelStorage modelStorage;
	private AntAPI antApi;
	
	public ClientAgent(DB db, ArrayList<ActorRef> arrayList) {
		log.info("C L I E N T started");
		
		this.neighbors = arrayList;
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.currentState = WaitingTaskState.getInstance((ClientAgentProxy) this);
		this.antApi = AntAPI.getInstance(context(), modelStorage);
	}
	
	@Override
	public void preStart() {
		log.debug("----->>>>" + self().path().toStringWithAddress(getContext().provider().getDefaultAddress()) + "<<<<<<<<-----");
		storage.persistClientAgentAddress(self().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("\n\n\nAdding neighbor to neighborlist" + message);
			neighbors.add((ActorRef) message);
		} else {
			log.debug("\n\n\nClientAgent, received Message" + message);
			currentState.onReceive(message, sender());
		}
	}
	
	public void unhandledMessage(Object message) {
		unhandled(message);
	}

	/**
	 * Broadcasts a message to all neighbors
	 * 
	 * @param msg
	 */
	public void broadcastToNeighbors(Object msg) {
		for (ActorRef neighbor: neighbors) {
			neighbor.tell(msg, self());
		}
	}
	
	public void setState(ClientAgentState state) {
		this.currentState = state;
//		this.currentState.run();
		addExpirationTimer(1, ClientAgentState.RUN); //this way I avoid having infinite calls for changing states after state
	}
	
	protected void addNeighbor(ActorRef actor) {
		neighbors.add(actor);
	}

	@Override
	public ModelStorage getModelStorage() {
		log.debug("Getting Model Storage");
		return modelStorage;
	}

	@Override
	public void addExpirationTimer(long time, final String message) {
//		system.scheduler().scheduleOnce(Duration.create(10, TimeUnit.SECONDS), getClientAgent(), system.dispatcher(), null);
		context().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, null);
					}
		}, context().system().dispatcher());
	}

	
	//TODO add abstraction to represent workflow
	@Override
	public ServiceRequestExploration getWorkflow() {
		return ServiceRequestExploration.getInstance("A", 10, self());
	}

	/**
	 * Selects the ActorRef with the lowest QoS
	 * TODO change return type to proper abstraction
	 *  
	 */
	@Override
	public ActorRef evaluateComposition(Map<ActorRef, QoSData> replies) {
		long min = Long.MAX_VALUE;
		ActorRef currentWinner = null;
		
		for (Map.Entry<ActorRef, QoSData> e: replies.entrySet()) {
			if (e.getValue().getComputationTime() <= min) {
				min = e.getValue().getComputationTime();
				currentWinner = e.getKey();
			}
		}
		
		assert currentWinner != null;
		return currentWinner;
	}
	
}
