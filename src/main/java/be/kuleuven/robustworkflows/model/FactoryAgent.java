package be.kuleuven.robustworkflows.model;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.mongodb.DB;

public class FactoryAgent extends UntypedActor implements EventDB {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private List<ActorRef> neigbhor;
	private DB db;

	public FactoryAgent(DB db, List<ActorRef> neighbors) {
		log.info("FactoryActor started");
		
		this.db = db;
		this.neigbhor = neighbors;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhor.add((ActorRef) message);
		}
	}

	@Override
	public void setDB(DB db) {
		this.db = db;
	}

}
