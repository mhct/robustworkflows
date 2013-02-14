package be.kuleuven.robustworkflows.infrastructure;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.EnvironmentConfiguration;

import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;

import com.mongodb.MongoClient;
import com.typesafe.config.ConfigFactory;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

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
	
	private static final String DEFAULT_SYSTEM_IP = "127.0.0.1";
	private static final String DEFAULT_SYSTEM_PORT = "29900";
	private static final String DEFAULT_SORCERER_NAME = "bilbo";
	private static final String SYSTEM_NAME = "RobustWorkflows";
	private static final String DEFAULT_DB_SERVER_IP = "127.0.0.1";
	private static final Integer DEFAULT_DB_SERVER_PORT = 27017;
	private static final String DEFAULT_DB_NAME = "workflows";
	
	private static final Configuration config = new EnvironmentConfiguration();
	private static final String SYSTEM_PORT = config.getString("SYSTEM_PORT", DEFAULT_SYSTEM_PORT);
	private static final String SYSTEM_IP = config.getString("SYSTEM_IP", DEFAULT_SYSTEM_IP);
	private static final String SORCERER_NAME = config.getString("SORCERER_NAME", DEFAULT_SORCERER_NAME);
	private static final String DB_SERVER_IP = config.getString("DB_SERVER_IP", DEFAULT_DB_SERVER_IP);
	private static final Integer DB_SERVER_PORT = config.getInteger("DB_SERVER_IP", DEFAULT_DB_SERVER_PORT);
	private static final String DB_NAME = config.getString("DB_NAME", DEFAULT_DB_NAME);
	
	
	private final ActorSystem system = ActorSystem.create(SYSTEM_NAME, ConfigFactory.parseString("akka.remote.netty.hostname=\""+SYSTEM_IP+"\"\nakka.remote.netty.port=\""+ SYSTEM_PORT + "\"").withFallback(ConfigFactory.load()));
	
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
