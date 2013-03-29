package be.kuleuven.robustworkflows.model.clientagent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;


import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.ModelStorage;
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

//	private final DB db;

	private ClientAgentState currentState;

	private InfrastructureStorage storage;

	private ModelStorage modelStorage;
	
	public ClientAgent(DB db, ArrayList<ActorRef> arrayList) {
		log.info("C L I E N T started");
		
		this.neighbors = arrayList;
//		this.db = db;
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.currentState = WaitingTaskState.getInstance((ClientAgentProxy) this);
	}
	
	@Override
	public void preStart() {
		storage.persistClientAgentAddress(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("Adding neighbor to neighborlist" + message);
			neighbors.add((ActorRef) message);
		} else {
			currentState.onReceive(message, sender());
		}
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
		addExpirationTimer(1, "run");
	}
	
	protected void addNeighbor(ActorRef actor) {
		neighbors.add(actor);
	}

	@Override
	public ModelStorage getModelStorage() {
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

	@Override
	public ServiceRequestExploration getWorkflow() {
		return ServiceRequestExploration.getInstance("A", 10, self());
	}
	
}
