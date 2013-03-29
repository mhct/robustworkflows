package be.kuleuven.robustworkflows.model.clientagent;

import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;
import akka.actor.ActorRef;

public interface ClientAgentProxy {

	public ActorRef self(); //return a reference to itself

	public void broadcastToNeighbors(Object msg); //broadcasts a message to neighboring nodes 

	public void setState(ClientAgentState state); //changes current state of the Agent
	
	public ModelStorage getModelStorage(); //gets the Model storage

	public void addExpirationTimer(long time, String message); //adds a Message to the list of future messages of the agent

	public ServiceRequestExploration getWorkflow();
}
