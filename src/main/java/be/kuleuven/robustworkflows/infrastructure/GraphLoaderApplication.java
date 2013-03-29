package be.kuleuven.robustworkflows.infrastructure;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.gephi.graph.api.DirectedGraph;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;
import be.kuleuven.robustworkflows.infrastructure.configuration.GephiGraphImporter;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class GraphLoaderApplication implements Bootable {
	
	private static final Config config = ConfigFactory.load().getConfig("graphloader");

	private static final String SYSTEM_NAME = config.getString("system-name");
	private static final String DB_SERVER_IP = config.getString("db-server-ip");
	private static final Integer DB_SERVER_PORT = config.getInt("db-server-port");
	private static final String DB_NAME = config.getString("db-name");
	private static final String NETWORK_MODEL = config.getString("network-model");
	
	
//	private final ActorSystem system = ActorSystem.create(SYSTEM_NAME, ConfigFactory.parseString("akka.remote.netty.hostname=\""+systemIp+"\"\nakka.remote.netty.port=\""+ systemPort + "\"").withFallback(ConfigFactory.load()));
	private final ActorSystem system;// = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
	private MongoClient mongoClient;
	private DirectedGraph networkModel;// = GephiGraphImporter.loadDirectedGraphFrom(NETWORK_MODEL);
	
	private ActorRef graphLoaderActor;

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
			mongoClient = new MongoClient(DB_SERVER_IP, DB_SERVER_PORT);
			final DB db = mongoClient.getDB(DB_NAME);
			final InfrastructureStorage storage = new InfrastructureStorage(db);
			
			mongoClient = new MongoClient(DB_SERVER_IP, DB_SERVER_PORT);
			final AgentFactory agentFactory = AgentFactory.getInstance();
			
			graphLoaderActor = system.actorOf(new Props(new UntypedActorFactory() {
				
				private static final long serialVersionUID = 2013020501L;
				
				@Override
				public Actor create() throws Exception {
					return new GraphLoaderActor(storage, networkModel, agentFactory);
				}
			}), "Gandalf");
			
			graphLoaderActor.tell("start", system.deadLetters());
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	

	public GraphLoaderApplication() {
		this.system = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
		try {
//			this.networkModel = GephiGraphImporter.loadDirectedGraphFrom(new File(getClass().getResource(NETWORK_MODEL).toURI()));
			File modelFile = new File(NETWORK_MODEL);
			this.networkModel = GephiGraphImporter.loadDirectedGraphFrom(modelFile);
//		} catch (URISyntaxException e) {
		} catch (NullPointerException e) {
			throw new RuntimeException("Invalid path of network model file: " + NETWORK_MODEL);
		}
	}
	
	public static void main(String[] args) throws IOException {
		GraphLoaderApplication app = new GraphLoaderApplication();
		app.startup();
		System.in.read();
		app.shutdown();
	}
	
}
