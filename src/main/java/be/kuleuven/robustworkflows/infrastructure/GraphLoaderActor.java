package be.kuleuven.robustworkflows.infrastructure;

import java.util.List;

import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.messages.DeployActorMsg;
import be.kuleuven.robustworkflows.model.FactoryActor;

import com.google.common.collect.Lists;

public class GraphLoaderActor extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	private final List<ActorRef> sorcerers;
	private final DirectedGraph networkModel;
	
	public GraphLoaderActor(List<String> sorcerersPaths, DirectedGraph networkModel) {
		if (sorcerersPaths == null || sorcerersPaths.size() == 0 || networkModel == null) {
			throw new IllegalArgumentException("Sorcerers Path can not be null");
		}
		sorcerers = loadSorcerers(sorcerersPaths);
		this.networkModel = networkModel;
	}
	
	@Override
	public void onReceive(Object message) {
		if (message.equals("start")) {
			log.info("GraphLoaderActor Started");
			
			//TODO redo adding the right places. The goal now is to check the load of a distributed system
			//load Sorcerers
			DeployActorMsg msg = new DeployActorMsg(new Props(FactoryActor.class), "factory1");
			for(ActorRef ref: sorcerers) {
				ref.tell(msg, getSelf());
			}
			
			loadActorsGraph();
		} else if (ActorRef.class.isInstance(message)) {
			ActorRef ref = (ActorRef) message;
			System.out.println("Received msg from: " + ref.path().name());
			ref.tell("teste", getSender());
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
			ret.add(ref);
		}
		
		return ret;
	}

	/**
	 * loads actors on remote machines according to the structure defined in networkModel
	 * 
	 * TODO how many actors per remote machine?
	 * 
	 */
	private void loadActorsGraph() {
		for(Node n: networkModel.getNodes()) {
			 System.out.println("node: " + n.getId());
		 }
		 
		 for(Edge e: networkModel.getEdges()) {
			 System.out.println("e: " +e.getId() + " :  "+ e.getSource().getId() + " -> " + e.getTarget().getId());
		 }
	}
}
