package be.kuleuven.robustworkflows.model;

import java.util.List;

import com.google.common.collect.Lists;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class FactoryActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private List<ActorRef> neigbhor;
	
	public FactoryActor() {
		log.info("FactoryActor started");
		neigbhor = Lists.newArrayList();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhor.add((ActorRef) message);
		}
	}

}
