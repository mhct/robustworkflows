package be.kuleuven.robustworkflows.model.ant;

import java.util.List;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.messages.Workflow;

import com.google.common.collect.Lists;

/**
 * API to interact with Ants
 * 
 * @author mariohct
 *
 */
public class AntAPI {
	private List<ActorRef> explorationAnts;
	private ActorContext context;
	private final ModelStorage modelStorage;
	private final ActorRef master;
	
	private AntAPI(ActorRef master, ActorContext context, ModelStorage modelStorage) {
		this.master = master;
		this.context = context;
		this.modelStorage = modelStorage;
		this.explorationAnts = Lists.newArrayList();
	}
	
	public void createExplorationAnt(final Workflow workflow) {
		explorationAnts.add(context.actorOf(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 2013021401L;

			@Override
			public Actor create() throws Exception {
				return ExplorationAnt.getInstance(master, modelStorage, workflow);
			}

		}), "explorationAnt" + explorationAnts.size()));
		
		startExplorationAnts();
		

	}

	private void startExplorationAnts() {
		for (ActorRef ant: explorationAnts) {
			ant.tell(EventType.RUN, master);
		}
	}
	
	public static AntAPI getInstance(ActorRef master, ActorContext context, ModelStorage storage) {
		return new AntAPI(master, context, storage);
	}

	public int explorationAnts() {
		return explorationAnts.size();
	}

	public void explore() {
		startExplorationAnts();
	}
}
