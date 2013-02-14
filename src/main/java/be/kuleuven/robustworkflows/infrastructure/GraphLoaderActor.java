package be.kuleuven.robustworkflows.infrastructure;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;
import be.kuleuven.robustworkflows.infrastructure.messages.ActorDeployRef;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployActorMsg;

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
	private final AgentFactory agentFactory;
	
	public GraphLoaderActor(List<String> sorcerersPaths, DirectedGraph networkModel, AgentFactory agentFactory) {

		log.debug("LLOADED GraphLoaderActor");
		this.agentFactory = agentFactory;
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
			
			loadActorsGraph();
			
		} else if (ActorDeployRef.class.isInstance(message)) {
			ActorDeployRef ref = (ActorDeployRef) message;
			
			networkModel.getNode(ref.getNodeName()).getAttributes().setValue("ActorRef", ref.getRef().path().toString());
			actors.put(ref.getNodeName(), ref.getRef());

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
	private void loadActorsGraph() throws Exception {
		
		//load actors remotely corresponding to each NodeType
		for(Node n: networkModel.getNodes()) {
			log.debug("node: " + n.getId());
			ActorRef sorcerer = getRandomSorcerer();
//			sorcerer.tell(new DeployActorMsg(new Props(agentFactory.handle(n.getAttributes().getValue("NodeType"))), n.getNodeData().getId()), getSelf()); //blocking operation
			sorcerer.tell(new DeployActorMsg("Factory", n.getNodeData().getId()), getSelf()); //blocking operation
		}
	}
}
