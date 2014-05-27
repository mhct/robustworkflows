package be.kuleuven.robustworkflows.model.antactors;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;

public interface ExplorationAntContext {

	ActorContext getContext();

	long getExplorationTimeout();

	double getSamplingProbability();

	ActorRef getCurrentAgent();

	void addAgentToVisitedNodes(ActorRef currentAgent);

	ActorRef getSelf();

	void setCurrentAgent(ActorRef actor);

	void tellMaster(Object instance);
	
	LoggingAdapter getLoggingAdapter();

}
