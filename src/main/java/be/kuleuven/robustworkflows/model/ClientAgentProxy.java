package be.kuleuven.robustworkflows.model;

import akka.actor.ActorRef;

public interface ClientAgentProxy {

	public ActorRef self(); //return a reference to itself

	public void broadcastToNeighbors(Object msg); //broadcasts a message to neighboring nodes 

	public void setState(ClientAgentState state); //changes current state of the Agent
}
