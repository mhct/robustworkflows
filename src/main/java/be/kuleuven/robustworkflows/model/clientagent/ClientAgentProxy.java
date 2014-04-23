package be.kuleuven.robustworkflows.model.clientagent;

import java.util.List;

import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ant.AntAPI;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.RequestExecutionData;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Workflow;

public interface ClientAgentProxy {

	//return a reference to itself
	public ActorRef self();
	
	public String clientAgentName();
	
	public String clientAgentCompleteName();
	
	//changes current state of the Agent
	public void setState(ClientAgentState state);
	
	//gets the Model storage
	public ModelStorage getModelStorage();

	//adds a Message to the list of future messages of the agent
	public void addExpirationTimer(long time, String message);

	public void unhandledMessage(Object message);

	//TODO change the result of the evaluation to proper abstraction
	public ExplorationResult evaluateComposition(List<ExplorationResult> replies);

	public AntAPI getAntAPI();

	public Workflow getWorkflow();
	
	public AgentAttributes getAttributes();

	public List<RequestExecutionData> getRequestsData();

	public void addRequestsExecutionData(RequestExecutionData requestData);

	public void clearRequestsData();

	public void setHackingState(ClientAgentState bla);
	
	public ClientAgentState getHackingState();
	
	public LoggingAdapter getLoggingAdapter();
}
