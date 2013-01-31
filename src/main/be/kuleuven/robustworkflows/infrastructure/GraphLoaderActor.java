package be.kuleuven.robustworkflows.infrastructure;

import java.util.List;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class GraphLoaderActor extends UntypedActor {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private List<String> sorcerers;
	
	public GraphLoaderActor() {
	}
	
	@Override
	public void onReceive(Object message) {
		if (message.equals("start")) {
			log.info("GraphLoaderActor Started");
			
			//TODO redo adding the right places. The goal now is to check the load of a distributed system
			//load Sorcerers
			String sorcererPath = "akka://RobustWorkflows@127.0.0.1:29000/user/bilbo";
			ActorRef sorcerer = getContext().actorFor(sorcererPath);
			
		}
		
	}
	
	/**
	 * loads actors on remote machines
	 * TODO how many actors per remote machine?
	 * 
	 * @param sorcerers
	 */
	private void loadActors(ActorRef sorcerer) {
		
	}

}
