package be.kuleuven.robustworkflows.model;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Provides computational services to any clients
 * version 0.1 doesn't have any capacity constraints, only a particular Average computational time
 * @author mario
 *
 */
public class FactoryAgent extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final List<ActorRef> neigbhor;
	private final DB db;

	private double computationTime;

	@Override
	public void preStart() {
		DBCollection coll = db.getCollection("factory_agents");
		DBObject obj = new BasicDBObject();
		obj.put("Current Time", System.currentTimeMillis());
		obj.put("ActorName", getSelf().path().name());
		
		coll.insert(obj);
	}
	
	public FactoryAgent(DB db, List<ActorRef> neighbors) {
		log.info("FactoryActor started");
		
		this.db = db;
		this.neigbhor = neighbors;
	}
	
	public FactoryAgent(DB db, List<ActorRef> neighbors, double computationTime) {
		log.info("FactoryActor started");
		
		this.db = db;
		this.neigbhor = neighbors;
		this.computationTime = computationTime;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("Adding neighbor to neighborlist" + message);
			neigbhor.add((ActorRef) message);
		} else if (ExplorationAnt.class.isInstance(message)) {
			
		} else {
			unhandled(message);
		}
		
	}

}
