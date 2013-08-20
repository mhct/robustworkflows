package be.kuleuven.robustworkflows.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.kernel.Bootable;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RobustWorkflowsLauncher implements Bootable {
	private LoggingAdapter log;
	
	private static final Config config = ConfigFactory.load().getConfig("robust-workflows-launcher");
	private static final String SYSTEM_NAME = config.getString("system-name");
	private static final String DB_SERVER_IP = config.getString("db-server-ip");
	private static final Integer DB_SERVER_PORT = config.getInt("db-server-port");
	private static final String DB_NAME = config.getString("db-name");
	private static final String DB_USER = config.getString("db-user");
	private static final String DB_PASS = config.getString("db-pass");

	private final ActorSystem system;
	private DB db;
	private MongoClient mongoClient;
	private InfrastructureStorage storage;
	
	public RobustWorkflowsLauncher() {
		system = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
		log = Logging.getLogger(system, this);
	}
	
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
			db = mongoClient.getDB(DB_NAME);
			if ( (!DB_USER.equals("") && !DB_PASS.equals("")) && !db.authenticate(DB_USER, DB_PASS.toCharArray()) ) {
				throw new RuntimeException("Couldn't authenticate to the Mongodb server");
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		storage = new InfrastructureStorage(db);
		
		sendComposeToAllClientAgents();
	}
	
	public void sendComposeToAllClientAgents() {
		log.debug("Searching Clients to send Compose Message");
		DBCursor cursor = storage.getClientAgent().find();
		String ref = "";
		long i=5;
		while (cursor.hasNext()) {	
			ref = (String) cursor.next().get("address");
			sendComposeMessage(system.actorFor(ref), i);
			i += 0;
		}
	}
	
	private void sendComposeMessage(final ActorRef ref, long time) {
		log.debug("Sending ComposeMessage");
		system.scheduler().scheduleOnce(Duration.create(time, TimeUnit.SECONDS), 
				new Runnable() {

					@Override
					public void run() {
						System.out.println("Enviando Compose Message");
						ref.tell("Compose", system.deadLetters());
					}
			
		}, system.dispatcher());		
	}

	public ActorSystem getSystem() {
		return system;
	}
	
	public static void main(String[] args) throws IOException {
		RobustWorkflowsLauncher wf = new RobustWorkflowsLauncher();
		wf.startup();
		wf.sendComposeToAllClientAgents();
//		Interpreter bsh = new Interpreter(new InputStreamReader(System.in), System.out, System.err, true);
//		bsh.run();
//		System.in.read();
//		wf.shutdown();
	}
}
