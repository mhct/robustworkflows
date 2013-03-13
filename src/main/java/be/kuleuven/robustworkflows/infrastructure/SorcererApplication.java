package be.kuleuven.robustworkflows.infrastructure;

import java.io.IOException;
import java.net.UnknownHostException;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;

import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * The Sorcerer is responsible for instantiating a SorcererActor
 * 
 * @author mario
 *
 *	EnvironmentVariables 
 *	@param SYSTEM_PORT port the system will listen to connections
 *	@param SYSTEM_IP IP Address of the host where the system is being deployed
 *	@param SORCERER_NAME Name of the Sorcerer Actor 
 *
 */
public class SorcererApplication implements Bootable {
	
	private static final Config config = ConfigFactory.load().getConfig("sorcerer");

	private static final String SYSTEM_NAME = config.getString("system-name");
	private static final String DB_SERVER_IP = config.getString("db-server-ip");
	private static final Integer DB_SERVER_PORT = config.getInt("db-server-port");
	private static final String DB_NAME = config.getString("db-name");
	private static final String SORCERER_NAME = config.getString("sorcerer-name");
	
	private final ActorSystem system = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
	
	private MongoClient mongoClient;
	private ActorRef sorcererActor;
			
	@Override
	public void shutdown() {
		system.shutdown();
		mongoClient.close();
	}

	@Override
	public void startup() {
		try {
			mongoClient = new MongoClient(DB_SERVER_IP, DB_SERVER_PORT);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		final AgentFactory agentFactory = AgentFactory.getInstance();
		
		sorcererActor = system.actorOf(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 2013020101L;

			@Override
			public Actor create() throws Exception {
				return new SorcererActor(mongoClient, DB_NAME, agentFactory);
			}
		}), SORCERER_NAME);
		
		sorcererActor.tell("start", system.deadLetters());
	}
	
	public static void main(String[] args) throws IOException {
		SorcererApplication app = new SorcererApplication();
		app.startup();
		System.in.read();
		app.shutdown();
	}

}
