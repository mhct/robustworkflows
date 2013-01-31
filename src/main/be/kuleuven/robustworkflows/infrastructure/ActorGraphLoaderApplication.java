package be.kuleuven.robustworkflows.infrastructure;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.EnvironmentConfiguration;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.kernel.Bootable;

public class ActorGraphLoaderApplication implements Bootable {

	private static final String DEFAULT_SYSTEM_IP = "127.0.0.1";
	private static final String DEFAULT_SYSTEM_PORT = "28800";
	private static final String DEFAULT_SORCERER_NAME = "bilbo";
	private static final String SYSTEM_NAME = "RobustWorkflows";
	
	private static final Configuration config = new EnvironmentConfiguration();
	private static final String systemPort = config.getString("SYSTEM_PORT", DEFAULT_SYSTEM_PORT);
	private static final String systemIp = config.getString("SYSTEM_IP", DEFAULT_SYSTEM_IP);
	private static final String sorcererName = config.getString("SORCERER_NAME", DEFAULT_SORCERER_NAME);
			
	private final ActorSystem system = ActorSystem.create(SYSTEM_NAME, ConfigFactory.parseString("akka.remote.netty.hostname=\""+systemIp+"\"\nakka.remote.netty.port=\""+ systemPort + "\"").withFallback(ConfigFactory.load()));
	private ActorRef graphLoaderActor;
			
	@Override
	public void shutdown() {
		system.shutdown();
	}

	@Override
	public void startup() {
		graphLoaderActor = system.actorOf(new Props(GraphLoaderActor.class), sorcererName);
		graphLoaderActor.tell("start", system.deadLetters());
	}

}
