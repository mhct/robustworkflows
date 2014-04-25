package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;

import com.google.common.collect.Lists;

import akka.actor.ActorRef;

public class Neighbors implements Serializable {
	private static final long serialVersionUID = 20130821L;
	
	private final List<ActorRef> neighbors;
	
	private Neighbors(List<ActorRef> neighbors) {
		this.neighbors = Lists.newArrayList(neighbors);
	}
	
	public List<ActorRef> getNeighbors() {
		return Lists.newArrayList(neighbors);
	}
 	
	public ActorRef getRandomNeighbor() {
		final RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister());
		return (ActorRef) random.nextSample(neighbors, 1)[0];
	}
	
	public static Neighbors getInstance(List<ActorRef> neighbors) {
		return new Neighbors(neighbors);
	}
}
