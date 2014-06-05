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

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
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
	private static final String DB_USER = config.getString("db-user");
	private static final String DB_PASS = config.getString("db-pass");
	
	private static final String SORCERER_NAME = config.getString("sorcerer-name");
	
	private static ActorSystem system;// = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
	
	private MongoClient mongoClient;
	private DB db;
	private static ActorRef sorcererActor;
			
	@Override
	public void shutdown() {
		system.shutdown();
		if(mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void startup() {
		try {
			mongoClient = new MongoClient(new ServerAddress(DB_SERVER_IP, DB_SERVER_PORT), new MongoClientOptions.Builder().connectionsPerHost(20).build());
			db = mongoClient.getDB(DB_NAME);
			db.setReadPreference(ReadPreference.secondaryPreferred()); //defaults to read from replicas on the cluster
			if ( (!DB_USER.equals("") && !DB_PASS.equals("")) && !db.authenticate(DB_USER, DB_PASS.toCharArray()) ) {
				throw new RuntimeException("Couldn't authenticate to the Mongodb server");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		final AgentFactory agentFactory = AgentFactory.getInstance();
		
		sorcererActor = system.actorOf(Props.create(SorcererActor.class, db, agentFactory), SORCERER_NAME);
		System.out.println("Sorcerer name: " + SORCERER_NAME);
		sorcererActor.tell("start", system.deadLetters());
	}
	
	public SorcererApplication() {
		this.system = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		SorcererApplication app = new SorcererApplication();
		app.startup();
//		for (int i=0; i<10000; i++) {
//			Thread.sleep(100);
//			sorcererActor.tell("start", system.deadLetters());
////			System.out.println("aqui vai " + i);
//		}
		System.in.read();
		app.shutdown();
		
	}

}
