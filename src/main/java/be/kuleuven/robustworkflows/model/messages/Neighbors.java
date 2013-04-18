package be.kuleuven.robustworkflows.model.messages;

import java.util.List;

import com.google.common.collect.Lists;

import akka.actor.ActorRef;

public class Neighbors {
	private final List<ActorRef> neighbors;
	
	private Neighbors(List<ActorRef> neighbors) {
		this.neighbors = Lists.newArrayList(neighbors);
	}
	
	public List<ActorRef> getNeighbors() {
		return Lists.newArrayList(neighbors);
	}
 	
	public static Neighbors getInstance(List<ActorRef> neighbors) {
		return new Neighbors(neighbors);
	}
}
