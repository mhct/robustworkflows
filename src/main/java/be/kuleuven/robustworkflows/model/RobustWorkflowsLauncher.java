package be.kuleuven.robustworkflows.model;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ReadPreference;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class RobustWorkflowsLauncher implements Bootable {
//	private LoggingAdapter log;
	
	private static final Config config = ConfigFactory.load().getConfig("robust-workflows-launcher");
	private static final String SYSTEM_NAME = config.getString("system-name");
	private static final String DB_SERVER_IP = config.getString("db-server-ip");
	private static final Integer DB_SERVER_PORT = config.getInt("db-server-port");
	private static final String DB_NAME = config.getString("db-name");
	private static final String DB_USER = config.getString("db-user");
	private static final String DB_PASS = config.getString("db-pass");
	
	private static final int CHECK_COMPLETION_RUN_INTERVAL = 180;
	
	private final ActorSystem system;
	private MongoClient mongoClient;
	
	public RobustWorkflowsLauncher() {
		system = ActorSystem.create(SYSTEM_NAME, config.withFallback(ConfigFactory.load()));
//		log = Logging.getLogger(system, this);
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
			final DB db = mongoClient.getDB(DB_NAME);
			db.setReadPreference(ReadPreference.secondaryPreferred());
			if ( (!DB_USER.equals("") && !DB_PASS.equals("")) && !db.authenticate(DB_USER, DB_PASS.toCharArray()) ) {
				throw new RuntimeException("Couldn't authenticate to the Mongodb server");
			}

			final ActorRef loader = system.actorOf(
					new Props(
						new UntypedActorFactory() {
							private static final long serialVersionUID = 20130926L;

							@Override
							public Actor create() throws Exception {
								return new RobustWorkflowsActor(db, config.getConfig("actor"));
							}
						}));
			
			system.scheduler().scheduleOnce(Duration.Zero(),
					new Runnable() {
						@Override
						public void run() {
							loader.tell("Start", system.deadLetters());
						}
			}, system.dispatcher());
			
			system.scheduler().schedule(Duration.Zero(), Duration.create(CHECK_COMPLETION_RUN_INTERVAL, TimeUnit.SECONDS),
					new Runnable() {
						@Override
							public void run() {
							loader.tell("CheckExecution", system.deadLetters());
						}
			}, system.dispatcher());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
//	public ActorSystem getSystem() {
//		return system;
//	}
	
	public static void main(String[] args) throws IOException {
		RobustWorkflowsLauncher wf = new RobustWorkflowsLauncher();
		wf.startup();
		System.in.read();
		wf.shutdown();
	}
//	public static void main(String[] args) throws IOException, InterruptedException {
//		RobustWorkflowsLauncher wf = new RobustWorkflowsLauncher();
//		wf.startup();
//		Thread.sleep(600000);
////		Interpreter bsh = new Interpreter(new InputStreamReader(System.in), System.out, System.err, true);
////		bsh.run();
////		System.in.read();
////		wf.shutdown();
////		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(8989));
////		for (int i=0; i<20; i++) {
////			System.out.println(i + ": " + random.nextPoisson(8));
////		}
//	}
}
