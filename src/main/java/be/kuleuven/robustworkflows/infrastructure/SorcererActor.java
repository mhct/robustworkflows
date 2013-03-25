package be.kuleuven.robustworkflows.infrastructure;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;
import be.kuleuven.robustworkflows.infrastructure.messages.ActorDeployRef;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployActorMsg;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

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
	private final AgentFactory agentFactory;
	private InfrastructureStorage storage; 
	
	public SorcererActor(MongoClient mongoClient, String dbName, AgentFactory agentFactory) {
		log.debug("SorcererActor loaded");
		log.info("SorcererActor loaded R E A L L Y");
		
		this.mongoClient = mongoClient;
		this.adminDB = mongoClient.getDB(dbName);
		this.agentFactory = agentFactory;
		this.storage = new InfrastructureStorage(adminDB);
	}

	@Override
	public void onReceive(Object message) {
		if(message.equals("start")) {
			log.info("Sorcerer started" + getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
//			DBCollection collection = adminDB.getCollection("sorcerers");
//			collection.insert(new BasicDBObject("sorcererPath", getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress())));
			storage.persistSorcererAddress(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
			
		} else if (DeployActorMsg.class.isInstance(message)) {
			log.debug("DeployActorMsg received" + message);
			final DeployActorMsg msg = DeployActorMsg.valueOf(message);
			ActorRef childActor = getContext().actorOf(new Props(new UntypedActorFactory() {
				
				private static final long serialVersionUID = 2013021401L;

				@Override
				public Actor create() throws Exception {
					return agentFactory.handleInstance(msg.getAgentType(), adminDB);
				}
			}), msg.getName());
			
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
