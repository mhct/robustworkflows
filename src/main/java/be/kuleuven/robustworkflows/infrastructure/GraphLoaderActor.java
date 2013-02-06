package be.kuleuven.robustworkflows.infrastructure;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.pattern.Patterns;
import akka.remote.RemoteActorRef;
import akka.util.Timeout;
import be.kuleuven.robustworkflows.infrastructure.messages.ActorDeployRef;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployActorMsg;
import be.kuleuven.robustworkflows.model.FactoryActor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GraphLoaderActor extends UntypedActor {
	
	private static final int SEED_SORCERER_SELECTION = 76544344;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final List<ActorRef> sorcerers;
	private final Map<String, ActorRef> actors;
	private final DirectedGraph networkModel;
	private final RandomDataGenerator random;
	private int deployedActors = 0;
	
	public GraphLoaderActor(List<String> sorcerersPaths, DirectedGraph networkModel) {
		log.debug("LLLLLLLLLLLLLLLLLLLLLLLLOADED GraphLoaderActor");
		if (sorcerersPaths == null || sorcerersPaths.size() == 0 || networkModel == null) {
			throw new IllegalArgumentException("Sorcerers Path can not be null");
		}
		random = new RandomDataGenerator(new MersenneTwister(SEED_SORCERER_SELECTION)); 
		sorcerers = loadSorcerers(sorcerersPaths);
		this.networkModel = networkModel;
		this.actors = Maps.newTreeMap();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {

		if (message.equals("start")) {
			log.info("GraphLoaderActor Started");
			
			//TODO redo adding the right places. The goal now is to check the load of a distributed system
			//load Sorcerers
//			DeployActorMsg msg = new DeployActorMsg(new Props(FactoryActor.class), "factory1");
//			for(ActorRef ref: sorcerers) {
//				ref.tell(msg, getSelf());
//			}
			
			final ActorRef sorcerer = getRandomSorcerer();
			loadActorsGraph(sorcerer);
			
		} else if (ActorDeployRef.class.isInstance(message)) {
			ActorDeployRef ref = (ActorDeployRef) message;
			System.out.println("!!!!!!!! Received msg from: " + ref.getNodeName());
			System.out.println("!!!!!!!! Ref: " + ref.getRef().getClass());
			ref.getRef().tell(getSelf(), getSelf());
			System.out.println("!!!!!!!! SENT reference to remote actore");
			
			// to complete network graph model
			Node temp = networkModel.getNode(ref.getNodeName());
			System.out.println("Node: " + temp);
//			System.out.println("Node: " + temp.getAttributes().getValue("NodeType"));
			
			networkModel.getNode(ref.getNodeName()).getAttributes().setValue("ActorRef", ref.getRef().path().toString());
			actors.put(ref.getNodeName(), ref.getRef());
//			ActorRef sourceTmp = (RemoteActorRef) networkModel.getNode(ref.getNodeName()).getAttributes().getValue("ActorRef");
//			System.out.println("sourceTMp: " + networkModel.getNode(ref.getNodeName()).getAttributes().getValue("ActorRef"));
			
			//ADD REFERENCES TO REMOTE ACTORS
			deployedActors++;
			if (networkModel.getNodeCount() == deployedActors) {
				for(Edge e: networkModel.getEdges()) {
					log.info("e: " +e.getId() + " :  "+ e.getSource().getId() + " -> " + e.getTarget().getId());
					ActorRef source = actors.get(e.getSource().getNodeData().getId());
					ActorRef target = actors.get(e.getTarget().getNodeData().getId());
					source.tell(target, getSelf());
				}
			}
		}
		
	}
	
	
	/**
	 * Creates a list of sorcerers actors available on remote machines
	 * 
	 * @param sorcerersList List with address of accessible sorcerers 
	 * 
	 */
	private List<ActorRef> loadSorcerers(List<String> sorcerersPaths) {
		
		List<ActorRef> ret = Lists.newArrayList();
		
		for (String sorcererPath: sorcerersPaths) {
			System.out.println("Loading actorFor:" + sorcererPath);
			ActorRef ref = getContext().actorFor(sorcererPath);
			System.out.println(ref.path().name());
			if (!ref.isTerminated()) {
				ret.add(ref);
			} else {
				log.debug("Not adding " + sorcererPath);
			}
		}
		
		if (ret.size() == 0) {
			throw new RuntimeException("No available sorcerer were found");
		}
		
		return ret;
	}

	private ActorRef getRandomSorcerer() {
		
		if (sorcerers.size() == 0) {
			throw new RuntimeException("There are no sorcerers available");
		
		} else if (sorcerers.size() == 1) {
			return sorcerers.get(0);
		
		} else {
			return sorcerers.get(random.nextInt(0, sorcerers.size()-1));
		} 
		
	}
	
	/**
	 * loads actors on remote machines according to the structure defined in networkModel
	 * 
	 * TODO how many actors per remote machine?
	 * @throws Exception in the case it is not possible to load one of the actors
	 * 
	 */
	private void loadActorsGraph(ActorRef sorcerer) throws Exception {
		
		Timeout timeout = new Timeout(Duration.create(5, "seconds"));
	
		if (sorcerer == null) {
			throw new IllegalArgumentException("Sorcerer can not be null");
		}
		
		
		//load actors remotely corresponding to each NodeType
		//TODO use a chain of responsibility with handlers for each NodeType
		for(Node n: networkModel.getNodes()) {
			log.debug("node: " + n.getId());

			//blocking operations
//			Future<Object> future = Patterns.ask(sorcerer, new DeployActorMsg(new Props(FactoryActor.class), n.getNodeData().getId()), timeout); //blocking operation
//			ActorRef result = (ActorRef) Await.result(future, timeout.duration());
			sorcerer.tell(new DeployActorMsg(new Props(FactoryActor.class), n.getNodeData().getId()), getSelf()); //blocking operation
//			n.getAttributes().setValue("ActorRef", result);
		}
		 
//		connect the actors by sending references to each other
//		for(Edge e: networkModel.getEdges()) {
//			log.info("e: " +e.getId() + " :  "+ e.getSource().getId() + " -> " + e.getTarget().getId());
//			Future<Object> future = Patterns.ask((ActorRef)e.getTarget().getAttributes().getValue("ActorRef"),
//												(ActorRef)e.getSource().getAttributes().getValue("ActorRef"), timeout);
//			log.info("Result: " + (String) Await.result(future, timeout.duration()));
//		}
	}
}
