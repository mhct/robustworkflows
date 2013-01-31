package be.kuleuven.robustworkflows.infrastructure;

import be.kuleuven.robustworkflows.infrastructure.messages.DeployActorMsg;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * The SorcererActor is responsible for creating other actors
 * 
 * @author mario
 *
 */
public class SorcererActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this); 
	
	@Override
	public void onReceive(Object message) {
		if(message.equals("start")) {
			log.info("Sorcerer started");
			
		} else if (DeployActorMsg.class.isInstance(message)) {
			final DeployActorMsg msg = DeployActorMsg.valueOf(message);
			ActorRef childActor = getContext().actorOf(msg.getProps(), msg.getName());
			
			getSender().tell(childActor, getSelf());
		} else {
			log.debug("Not handling message" + message);
			unhandled(message);
		}
		
	}
}
