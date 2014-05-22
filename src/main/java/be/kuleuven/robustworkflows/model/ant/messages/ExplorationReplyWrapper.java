package be.kuleuven.robustworkflows.model.ant.messages;

import java.io.Serializable;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.messages.ExplorationReply;

public class ExplorationReplyWrapper implements Serializable {
	private static final long serialVersionUID = 20140506L;
	
	private ExplorationReply reply;
	private ActorRef actor;

	public ExplorationReply getReply() {
		return reply;
	}
	
	public ActorRef getActor() {
		return actor;
	}
	
	public ExplorationReplyWrapper (ActorRef actor, ExplorationReply reply) {
		this.actor = actor;
		this.reply = reply;
	}

	public static ExplorationReplyWrapper getInstance(ActorRef actor, ExplorationReply reply) {
		if (reply == null) {
			reply = ExplorationReply.notPossible(null);
		}
		
		return new ExplorationReplyWrapper(actor, reply);
	}
	

	public static ExplorationReplyWrapper empty() {
		return new ExplorationReplyWrapper(null, null);
	}

	@Override
	public String toString() {
		return "ExplorationReplyWrapper [reply=" + reply + ", actor=" + actor
				+ "]";
	}

	public boolean isPossible() {
		if (reply == null) {
			return false;
		} else {
			return reply.isPossible();
		}
	}
}