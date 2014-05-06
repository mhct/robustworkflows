package be.kuleuven.robustworkflows.model.antactors;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.ant.messages.ExploreService;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.EmptyExplorationResult;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.util.State;
import be.kuleuven.robustworkflows.util.Utils;

public class ExploringState implements State<Object, ExplorationAntContext>{

	private ActorRef talker;
	private Neighbors neighbors;

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object handle(Object event, ExplorationAntContext context) {
		if (ExploreService.class.isInstance(event)) {
			Utils.addExpirationTimer(context.getContext(), context.getExplorationTimeout(), EventType.ExploringStateTimeout);
			final ExploreService msg = (ExploreService) event;
			
			talker = context.getContext().actorOf(TalkerAntActor.props(msg.getServiceType(),
					(long) Math.ceil(context.getExplorationTimeout()/4.0),
					context.getSamplingProbability()));
			context.addToVisitedNodes(context.getCurrentAgent());
			context.tellMaster(EventType.NeihgborListRequest);
			System.out.println(name() + " handling: " + event);
	
		} else if (Neighbors.class.isInstance(event)){
//			log.debug("Received Neighbors list");
			neighbors = (Neighbors) event;
//			//explore neighbors
			talker.tell(neighbors, context.getSelf());
			
		} else if (ExplorationReplyWrapper.class.isInstance(event)) {
			ExplorationReplyWrapper wrapper = (ExplorationReplyWrapper) event;
			if (wrapper.getReply().isPossible()) {
				//jumps to the agent which has the best reply
//				context.setCurrentAgent(wrapper.getActor());
				context.tellMaster(SimpleExplorationResult.getInstance((ExplorationReplyWrapper) event));
				return "Idle";
			} else {
				//Searches again. The exploration strategy could be made here. Either cloning, ACO, etc.
				// Initial strategy, randomly select one neighbor as jump to it.
				neighbors.getRandomNeighbor().tell(EventType.NeihgborListRequest, context.getSelf());
			}
			
		} else if (EventType.ExploringStateTimeout.equals(event)) {
			context.tellMaster(new EmptyExplorationResult());
			return "Idle";
		}
		
		return null;
	}

	@Override
	public void onEntry(Object event, ExplorationAntContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Object event, ExplorationAntContext context) {
		// TODO Auto-generated method stub
		
	}

}
