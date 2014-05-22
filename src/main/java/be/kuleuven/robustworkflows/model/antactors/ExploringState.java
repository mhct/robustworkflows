package be.kuleuven.robustworkflows.model.antactors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.ant.messages.ExploreService;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.util.State;
import be.kuleuven.robustworkflows.util.Utils;

/**
 * Explore until finding 5 candidate services of the required {@link ServiceType}.
 * 
 * @author mario
 *
 */
public class ExploringState implements State<Object, ExplorationAntContext>{

	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	private static final int WAIT_FOR_NUMBER_REPLIES = 5;
	private ActorRef talker;
	private Neighbors neighbors;
	private final ExplorationRepliesHolder repliesHolder;


	public ExploringState() {
		repliesHolder = ExplorationRepliesHolder.getInstance();	
	}
	
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
					(long) Math.ceil(context.getExplorationTimeout()/10.0),
					context.getSamplingProbability()));
			context.addToVisitedNodes(context.getCurrentAgent());
			context.tellMaster(EventType.NeihgborListRequest);
			System.out.println(name() + " handling: " + event);
	
		} else if (Neighbors.class.isInstance(event)){
//			log.debug("Received Neighbors list");
			neighbors = (Neighbors) event;
//			//asks talker to retrieve QoS of neighbors
			talker.tell(neighbors, context.getSelf());
			
		} else if (ImmutableExplorationRepliesHolder.class.isInstance(event)) {
			context.getLoggingAdapter().debug("ImmutableExplorationRepliesHolder received");
			ImmutableExplorationRepliesHolder replies = (ImmutableExplorationRepliesHolder) event;
			
			for (ExplorationReplyWrapper erw: replies) {
				repliesHolder.add(erw);
			}
			
			if (repliesHolder.atLeastNbReplies(WAIT_FOR_NUMBER_REPLIES)) {
				context.tellMaster(SimpleExplorationResult.getInstance(repliesHolder.bestExplorationReply()));
				return "Idle";
			} else {
				//Searches again. The exploration strategy could be made here. Either cloning, ACO, etc.
				// Initial strategy, randomly select one neighbor as jump to it.
				ActorRef next = neighbors.getRandomNeighbor();
				
				if (next != null) {
					next.tell(EventType.NeihgborListRequest, context.getSelf());
				}
			}
			
		} else if (EventType.ExploringStateTimeout.equals(event)) {
			context.getLoggingAdapter().debug("TIMEOUT :" + dtf.print(new DateTime()));
//			if (repliesHolder.isEmpty()) {
//				context.tellMaster(new EmptyExplorationResult());
//				
//				
//			} else {
//				context.tellMaster(SimpleExplorationResult.getInstance(repliesHolder.bestExplorationReply()));
//			}
			return "Idle";
		} else {
			context.getLoggingAdapter().debug("Unhandled message: " + event);
			
		}
		
		return null;
	}

	@Override
	public void onEntry(Object event, ExplorationAntContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Object event, ExplorationAntContext context) {
		repliesHolder.clear();
	}

}
