package be.kuleuven.robustworkflows.infrastructure;

import be.kuleuven.robustworkflows.infrastructure.messages.ActorDeployRef;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployActorMsg;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * The SorcererActor is responsible for creating other actors
 * 
 * @author mario
 *
 */
public class SorcererActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final MongoClient mongoClient;
	private final DB adminDB; 
	
	public SorcererActor(MongoClient mongoClient, String dbName) {
		log.debug("SorcererActor loaded");
		log.info("SorcererActor loaded R E A L L Y");
		
		this.mongoClient = mongoClient;
		this.adminDB = mongoClient.getDB(dbName);
	}

	@Override
	public void onReceive(Object message) {
		if(message.equals("start")) {
			log.info("Sorcerer started" + getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
			DBCollection collection = adminDB.getCollection("sorcerers");
			collection.insert(new BasicDBObject("sorcererPath", getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress())));
			
		} else if (DeployActorMsg.class.isInstance(message)) {
			log.debug("DeployActorMsg received" + message);
			final DeployActorMsg msg = DeployActorMsg.valueOf(message);
			ActorRef childActor = getContext().actorOf(msg.getProps(), msg.getName());
			
			if(childActor == null) {
				System.out.println("PROBLEM: childActor is null");
			}
			
			getSender().tell(new ActorDeployRef(childActor, msg.getName()), getSelf());
		} else {
			log.debug("Not handling message" + message);
			unhandled(message);
		}
		
	}
}
