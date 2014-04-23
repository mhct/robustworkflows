package be.kuleuven.dmas;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.Neighbors;

import com.google.common.collect.ImmutableList;

/**
 * DelegateMAS middleware
 * 
 * This class provides the interface needed to create a DelegateMAS (check papers) agent solution.
 * 
 * @author mario
 *
 */
public class DmasMW {

	private final LoggingAdapter log;
	private final ActorRef master;
	private final AgentType agentType;
	private final List<ActorRef> neighbors;

	private DmasMW(ActorSystem system, ActorRef master, AgentType agentType) {
		this.master = master;
		this.agentType = agentType;
		
		neighbors = new ArrayList<ActorRef>();
		log = Logging.getLogger(system, master);
	}
	
	/**
	 * Factory method that create a DmasMW instance
	 * 
	 * @param master
	 * @param agentType
	 * @return
	 */
	public static DmasMW getInstance(ActorSystem system, ActorRef master, AgentType agentType) {
		return new DmasMW(system, master, agentType);
	}

	public void addNeighbor(ActorRef neighbor) {
		log.debug("Adding Neighbor: " + neighbor);
		neighbors.add(neighbor);
	}
	
	public void removeNeighbor(ActorRef neighbor) {
		log.debug("Removing Neighbor: " + neighbor);
		neighbors.remove(neighbor);
	}
	
	public Neighbors getNeighbors() {
		log.debug("Retrieving List Neighbors");
		return Neighbors.getInstance(neighbors);
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public void addNeighbors(List<ActorRef> neighbors) {
		this.neighbors.addAll(ImmutableList.copyOf(neighbors));
	}
	
	public void onReceive(Object message, ActorRef sender) {
		log.debug("MESSAGE: " + message.toString());
		
		if(ActorRef.class.isInstance(message)) {
			log.debug("Received ActorRef");
			neighbors.add((ActorRef) message);
			
		} else if (EventType.NeihgborListRequest.equals(message)){
			log.debug("- 1 - Received NeighborListRequest");
			sender.tell(getNeighbors(), master);
		} else if (EventType.NeihgborListRequest.equals(message)){
			log.debug("- 2 - Received NeighborListRequest");
		sender.tell(getNeighbors(), master);
		}
	}


}
