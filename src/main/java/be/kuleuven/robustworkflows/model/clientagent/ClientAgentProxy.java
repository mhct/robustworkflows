package be.kuleuven.robustworkflows.model.clientagent;

import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ant.AntAPI;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;
import be.kuleuven.robustworkflows.model.messages.Workflow;

public interface ClientAgentProxy {

	public ActorRef self(); //return a reference to itself

//	public void broadcastToNeighbors(Object msg); //broadcasts a message to neighboring nodes 

	public void setState(ClientAgentState state); //changes current state of the Agent
	
	public ModelStorage getModelStorage(); //gets the Model storage

	public void addExpirationTimer(long time, String message); //adds a Message to the list of future messages of the agent

	public ServiceRequestExploration getServiceRequestExploration();

	public void unhandledMessage(Object message);

	public ExplorationResult evaluateComposition(List<ExplorationResult> replies); //TODO change the result of the evaluation to proper abstraction

	public AntAPI getAntAPI();

	public Workflow getWorkflow();
}
