package be.kuleuven.robustworkflows.model;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;

import com.mongodb.DB;

/**
 * 
 * @author mario
 *
 */
public class ClientAgent extends UntypedActor implements ClientAgentProxy {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final List<ActorRef> neighbors;

	private final DB db;

	private ClientAgentState currentState;

	private InfrastructureStorage storage;
	
	public ClientAgent(DB db, ArrayList<ActorRef> arrayList) {
		log.info("C L I E N T started");
		
		this.neighbors = arrayList;
		this.db = db;
		this.storage = new InfrastructureStorage(db);
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
	}
	
	protected void addNeighbor(ActorRef actor) {
		neighbors.add(actor);
	}
	
}
