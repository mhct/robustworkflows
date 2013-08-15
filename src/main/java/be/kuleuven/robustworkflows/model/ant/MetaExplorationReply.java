package be.kuleuven.robustworkflows.model.ant;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

public class MetaExplorationReply {

	private final ExplorationReply reply;
	private final ActorRef sender;
	
	private MetaExplorationReply(ActorRef sender, ExplorationReply reply) {
		this.sender = sender;
		this.reply = reply;
	}
	

	public ExplorationReply getReply() {
		return reply;
	}

	public ActorRef getSender() {
		return sender;
	}
	
	public static MetaExplorationReply getInstance(ActorRef sender, ExplorationReply reply) {
		return new MetaExplorationReply(sender, reply);
	}
	
}
