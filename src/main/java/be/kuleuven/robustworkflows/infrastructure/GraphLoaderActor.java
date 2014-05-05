package be.kuleuven.robustworkflows.infrastructure;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.configuration.AgentFactory;
import be.kuleuven.robustworkflows.infrastructure.messages.AgentDeployed;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployAgent;
import be.kuleuven.robustworkflows.model.AgentAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.DBCursor;

/**
 * Actor responsible for loading new agents.
 * 
 * @author mario
 *
 */
public class GraphLoaderActor extends UntypedActor {
	
	private static final int SEED_SORCERER_SELECTION = 76544344;
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final List<ActorRef> sorcerers;
	private final Map<String, ActorRef> actors;
	private final DirectedGraph networkModel;
	private final RandomDataGenerator random;
	private int deployedActors = 0;
	private InfrastructureStorage storage;
	
	public GraphLoaderActor(InfrastructureStorage storage, DirectedGraph networkModel, AgentFactory agentFactory) {
		log.debug("LOADED GraphLoaderActor");
		
		if (storage == null || networkModel == null) {
			throw new IllegalArgumentException("Sorcerers Path can not be null");
		}
		
		this.storage = storage;
		random = new RandomDataGenerator(new MersenneTwister(SEED_SORCERER_SELECTION)); 
		sorcerers = loadSorcerers(getSorcerersPaths(storage));
		this.networkModel = networkModel;
		this.actors = Maps.newTreeMap();
	}
	
	@Override
	public void onReceive(Object message) throws Exception {

		if (message.equals("start")) {
			log.info("GraphLoaderActor Started");
			
			//FIXME normally..should load agents on random nodes
//			loadActorsGraph();
			loadOneAgentPerSorcerer();
			
		} else if (AgentDeployed.class.isInstance(message)) {
			AgentDeployed ref = (AgentDeployed) message;
			
			networkModel.getNode(ref.getNodeName()).getAttributes().setValue("ActorRef", ref.getRef().path().toString());

			storage.persistActorAddress(ref.getRef().path().toString(), ref.getNodeName(), ref.getAgentType());
			actors.put(ref.getNodeName(), ref.getRef());

			//ADD REFERENCES TO REMOTE ACTORS
			deployedActors++;
			if (networkModel.getNodeCount() == deployedActors) {
				// loads edges in both directions
				for(Edge e: networkModel.getEdges()) {
					log.info("e: " +e.getId() + " :  "+ e.getSource().getId() + " -> " + e.getTarget().getId());
					ActorRef source = actors.get(e.getSource().getNodeData().getId());
					ActorRef target = actors.get(e.getTarget().getNodeData().getId());
					source.tell(target, getSelf());
					
					log.info("e: " +e.getId() + " :  "+ e.getTarget().getId() + " -> " + e.getSource().getId());
					target.tell(source, getSelf());
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
			log.info("Loading actorFor:" + sorcererPath);
			ActorRef ref = getContext().actorFor(sorcererPath);
			log.info(ref.path().name());
			if (!ref.isTerminated()) {
				ret.add(ref);
			} else {
				log.debug("Not adding " + sorcererPath);
			}
		}
		
		if (ret.size() == 0) {
			throw new RuntimeException("No available sorcerer was found");
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
	
	private List<String> getSorcerersPaths(InfrastructureStorage storage) {
		List<String> sorcerersPaths = Lists.newArrayList();
		
		DBCursor cursor = storage.getSorcerers().find();
		while (cursor.hasNext()) {
			String sorcererPath = (String) cursor.next().get("sorcererPath");
			log.debug("sorcererPath:" + sorcererPath);
			sorcerersPaths.add(sorcererPath);
		}
		
		return sorcerersPaths;
	}

	
	/**
	 * loads actors on remote machines according to the structure defined in networkModel
	 * FIXME the infrastructure (GraphLoader) has to inspect the Model to deploy different types of actors on different sorcerers
	 * TODO how many actors per remote machine?
	 * @throws Exception in the case it is not possible to load one of the actors
	 * 
	 */
//	private void loadActorsGraph() throws Exception {
//		//load actors remotely corresponding to each NodeType
//		for(Node n: networkModel.getNodes()) {
//			log.debug("node: " + n.getId());
//			ActorRef sorcerer = getRandomSorcerer();
//
//			//n.getNodeData() is a blocking operation
//			sorcerer.tell(DeployAgent.getInstance(AgentAttributes.getInstance(n.getNodeData().getAttributes(), n.getNodeData().getId())), getSelf());
//		}
//		
//	}

	/**
	 * Loads one agent per sorcerer... 
	 * FIXME this is a temporary code.
	 */
	private void loadOneAgentPerSorcerer() {
		int i=0;
		for(Node n: networkModel.getNodes()) {
			log.debug("node: " + n.getId());
			ActorRef sorcerer = sorcerers.get(i%sorcerers.size());
			sorcerer.tell(DeployAgent.getInstance(AgentAttributes.getInstance(n.getNodeData().getAttributes(), n.getNodeData().getId())), getSelf());
			i++;
		}
		
	}
}
