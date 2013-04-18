package be.kuleuven.robustworkflows.model.ant;

import java.util.List;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.Workflow;

import com.google.common.collect.Lists;

/**
 * API to interact with Ants
 * 
 * @author mariohct
 *
 */
public class AntAPI {
	List<ActorRef> explorationAnts = Lists.newArrayList();
	private ActorContext context;
	private final ModelStorage modelStorage;
	
	private AntAPI(ActorContext context, ModelStorage modelStorage) {
		this.context = context;
		this.modelStorage = modelStorage;
	}
	
	public void createExplorationAnt(final Workflow workflow) {
		explorationAnts.add(context.actorOf(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 2013021401L;

			@Override
			public Actor create() throws Exception {
				return ExplorationAnt.getInstance(modelStorage, workflow);
			}

		}), "explorationAnt" + explorationAnts.size()));

	}

	
	public static AntAPI getInstance(ActorContext context, ModelStorage storage) {
		return new AntAPI(context, storage);
	}
}
