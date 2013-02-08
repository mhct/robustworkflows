package be.kuleuven.robustworkflows.infrastructure;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.gephi.graph.api.DirectedGraph;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;
import be.kuleuven.robustworkflows.infrastructure.configuration.GephiGraphImporter;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.typesafe.config.ConfigFactory;

public class GraphLoaderApplication implements Bootable {
	
	private static final String DEFAULT_SYSTEM_IP = "127.0.0.1";
	private static final String DEFAULT_SYSTEM_PORT = "28800";
	private static final String SYSTEM_NAME = "RobustWorkflows";
	private static final String DEFAULT_DB_SERVER_IP = "127.0.0.1"; //put this name on a config file
	private static final Integer DEFAULT_DB_SERVER_PORT = 27017;
	private static final String DEFAULT_DB_NAME = "workflows"; //put this name on a config file
	private static final String DEFAULT_NETWORK_MODEL = "/simple500.gexf"; //put this name on a config file

	private static final Configuration config = new EnvironmentConfiguration();
	private static final String systemPort = config.getString("SYSTEM_PORT", DEFAULT_SYSTEM_PORT);
	private static final String systemIp = config.getString("SYSTEM_IP", DEFAULT_SYSTEM_IP);
	private static final String DB_SERVER_IP = config.getString("DB_SERVER_IP", DEFAULT_DB_SERVER_IP);
	private static final Integer DB_SERVER_PORT = config.getInteger("DB_SERVER_IP", DEFAULT_DB_SERVER_PORT);
	private static final String DB_NAME = config.getString("DB_NAME", DEFAULT_DB_NAME);
	private static final String NETWORK_MODEL = config.getString("NETWORK_MODEL", DEFAULT_NETWORK_MODEL);
	
	
	private final ActorSystem system;// = ActorSystem.create(SYSTEM_NAME, ConfigFactory.parseString("akka.remote.netty.hostname=\""+systemIp+"\"\nakka.remote.netty.port=\""+ systemPort + "\"").withFallback(ConfigFactory.load()));
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

			mongoClient = new MongoClient(DB_SERVER_IP, DB_SERVER_PORT);
			final AgentFactory agentFactory = AgentFactory.getInstance();
			
			graphLoaderActor = system.actorOf(new Props(new UntypedActorFactory() {
				
				private static final long serialVersionUID = 2013020501L;
				
				@Override
				public Actor create() throws Exception {
					return new GraphLoaderActor(getSorcerersPaths(db), networkModel, agentFactory);
				}
			}), "Gandalf");
			
			graphLoaderActor.tell("start", system.deadLetters());
		
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	private List<String> getSorcerersPaths(DB db) {
		List<String> sorcerersPaths = Lists.newArrayList();
		
		DBCursor cursor = db.getCollection("sorcerers").find();
		while (cursor.hasNext()) {
			String sorcererPath = (String) cursor.next().get("sorcererPath");
			System.out.println("sorcererPath:" + sorcererPath);
			sorcerersPaths.add(sorcererPath);
		}
		
		return sorcerersPaths;
	}

	public GraphLoaderApplication() {
		this.system = ActorSystem.create(SYSTEM_NAME, ConfigFactory.parseString("akka.remote.netty.hostname=\""+systemIp+"\"\nakka.remote.netty.port=\""+ systemPort + "\"").withFallback(ConfigFactory.load()));
		this.networkModel = GephiGraphImporter.loadDirectedGraphFrom(NETWORK_MODEL);
	}
	
	public static void main(String[] args) throws IOException {
		GraphLoaderApplication app = new GraphLoaderApplication();
		app.startup();
		System.in.read();
		app.shutdown();
	}
	
}
