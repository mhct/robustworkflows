package be.kuleuven.robustworkflows.model;

import scala.concurrent.duration.Duration;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.kernel.Bootable;

public class TimeTestBootable implements Bootable {

	private Config config = ConfigFactory.load().getConfig("robust-workflows-launcher");;
	private ActorSystem system;

	public TimeTestBootable() {
		system = ActorSystem.create("test", config.withFallback(ConfigFactory.load()));
	}
	
	@Override
	public void shutdown() {
		system.shutdown();
	}

	@Override
	//FIXME
	public void startup() {
		final ActorRef mySimpleActor = system.actorOf(
				Props.create(
					new UntypedActorFactory() {
						private static final long serialVersionUID = 20130926L;

						@Override
						public Actor create() throws Exception {
							return new SimpleActor(config.getConfig("actor"));
						}
					}));
		
		system.scheduler().scheduleOnce(Duration.Zero(),
				new Runnable() {
					@Override
					public void run() {
						mySimpleActor.tell("Start", system.deadLetters());
					}
		}, system.dispatcher());
	}

}
