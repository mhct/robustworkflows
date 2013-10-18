package be.kuleuven.robustworkflows.model.ant;

import java.util.List;

import akka.actor.Actor;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationBehaviorFactory;

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
	private final ActorRef master;
	private ExplorationBehaviorFactory behaviorFactory;
	
	private AntAPI(ExplorationBehaviorFactory behaviorFactory, ActorRef master, ActorContext context) {
		this.behaviorFactory = behaviorFactory;
		this.master = master;
		this.context = context;
		this.explorationAnts = Lists.newArrayList();
	}
	
	public void createExplorationAnt(final ExplorationAntParameter explorationAntParameters) {
		
		explorationAnts.add(context.actorOf(new Props(new UntypedActorFactory() {
			
			private static final long serialVersionUID = 2013021401L;

			@Override
			public Actor create() throws Exception {
				return behaviorFactory.createExplorationAnt(explorationAntParameters);
			}

		}), "explorationAnt" + explorationAnts.size()));
		
	}

	public static AntAPI getInstance(ExplorationBehaviorFactory behaviorFactory, ActorRef master, ActorContext context) {
		return new AntAPI(behaviorFactory, master, context);
	}

	public int explorationAnts() {
		return explorationAnts.size();
	}

	/**
	 * Sends a message to all exploration ants
	 * 
	 * @param message
	 * @param workflowTask
	 */
	public void tellAll(Object message) {
		for (ActorRef ant: explorationAnts) {
			ant.tell(message, master);
		}
	}
}
