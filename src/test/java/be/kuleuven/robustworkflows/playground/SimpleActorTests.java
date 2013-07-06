package be.kuleuven.robustworkflows.playground;

import java.util.ArrayList;

import scala.concurrent.Future;

import com.google.common.collect.Lists;

import static akka.pattern.Patterns.ask;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.dispatch.Futures;

public class SimpleActorTests {

	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("MyTestSystem");
		final ActorRef alex = system.actorOf(Props.apply(Robocop.class), "Alex");
		final ActorRef bob = system.actorOf(Props.apply(Robocop.class), "bob");

		final ArrayList<Future<Object>> futures = Lists.newArrayList();
		futures.add(ask(alex, "run", 1000));
		futures.add(ask(bob, "run", 1000));
		
		final Future<Iterable<Object>> aggregate = Futures.sequence(futures,  system.dispatcher());
	}
}

class Robocop extends UntypedActor {

	@Override
	public void onReceive(Object msg) throws Exception {
		
		if ("run".equals(msg)) {
//			getSender().tell("Thanks", getSelf());
			System.out.println(System.currentTimeMillis());
		}
		
	}
	
}
