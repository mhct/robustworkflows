package be.kuleuven.robustworkflows.model;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class FactoryActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	@Override
	public void onReceive(Object message) throws Exception {
		log.info("FactoryActor started");
	}

}
