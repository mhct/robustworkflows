package be.kuleuven.robustworkflows.model;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mongodb.DB;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientAgent extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final List<ActorRef> neigbhor;

	private final DB db;
	
	public ClientAgent(DB db, ArrayList<ActorRef> arrayList) {
		log.info("C L I E N T started");
		
		this.neigbhor = arrayList;
		this.db = db;
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
