package be.kuleuven.robustworkflows.model.antactors.dmas;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.event.LoggingAdapter;

public interface DMASExplorationAntContext {

	ActorContext getContext();

	long getExplorationTimeout();

	double getSamplingProbability();

	void addAgentToVisitedNodes(ActorRef currentAgent);

	ActorRef getSelf();

	void tellMaster(Object instance);
	
	LoggingAdapter getLoggingAdapter();

}
