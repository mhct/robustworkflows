package be.kuleuven.robustworkflows.infrastructure;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;
import be.kuleuven.robustworkflows.infrastructure.messages.AgentDeployed;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployAgent;

import com.mongodb.DB;

/**
 * The SorcererActor is responsible for creating other actors
 * 
 * @author mario
 *
 */
public class SorcererActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final DB adminDB;
	private final AgentFactory agentFactory;
	private InfrastructureStorage storage; 
	
	public SorcererActor(DB adminDB, AgentFactory agentFactory) {
		log.debug("SorcererActor loaded");
		log.info("SorcererActor loaded R E A L L Y");
		
//		this.mongoClient = mongoClient;
		this.adminDB = adminDB;
		
		this.agentFactory = agentFactory;
		this.storage = new InfrastructureStorage(adminDB);
	}

	@Override
	public void onReceive(Object message) {
		if(message.equals("start")) {
			log.info("BLE Sorcerer started" + getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
			storage.persistSorcererAddress(getSelf().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
			
		} else if (DeployAgent.class.isInstance(message)) {
			log.debug("DeployActorMsg received" + message);
			final DeployAgent msg = DeployAgent.valueOf(message);
			ActorRef childActor = getContext().system().actorOf(new Props(new UntypedActorFactory() {
				
				private static final long serialVersionUID = 2013021401L;

				@Override
				public Actor create() throws Exception {
					return agentFactory.handleInstance(msg.attributes()); 
				}
			}), msg.attributes().getAgentId());
			
			if(childActor == null) {
				log.error("PROBLEM: childActor is null");
			}

			//FIXME hack.. now the sorcerer knows about which type of agents the system has.. that is against the initial design rationale.
//			if (msg.attributes().getAgentType().equals("Client")) {
//				storage.persistClientAgentAddress(childActor.path().toStringWithAddress(getContext().provider().getDefaultAddress()), self().path().name());
//			} else {
//				storage.registerFactoryAgent(childActor.path().toStringWithAddress(getContext().provider().getDefaultAddress()), msg.attributes().getComputationalProfile().getServiceType());
//			}
			getSender().tell(new AgentDeployed(childActor, msg.attributes().getAgentId(), msg.attributes().getAgentType()), getSelf());
		} else {
			log.debug("Not handling message" + message);
			unhandled(message);
		}
		
	}
}
