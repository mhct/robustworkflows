package be.kuleuven.robustworkflows.model.antactors.dmas2;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
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
public class DMAS2ExploringState implements State<Object, DMAS2ExplorationAntContext>{

	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	private static final int WAIT_FOR_NUMBER_REPLIES = 5;
	private ActorRef talker;
	private Neighbors neighbors;
	private final DMAS2ExplorationRepliesHolder repliesHolder;


	public DMAS2ExploringState() {
		repliesHolder = DMAS2ExplorationRepliesHolder.getInstance();	
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object handle(Object event, DMAS2ExplorationAntContext context) {
		if (ExploreService.class.isInstance(event)) {
			Utils.addExpirationTimer(context.getContext(), context.getExplorationTimeout(), EventType.ExploringStateTimeout);
			final ExploreService msg = (ExploreService) event;
			
			if (talker != null) {
				talker.tell(PoisonPill.getInstance(), context.getSelf());
			}
			talker = context.getContext().actorOf(Props.create(new DMAS2TalkerAntActor.TalkerAntActorCreator(msg.getServiceType(),
					(long) Math.ceil(context.getExplorationTimeout()/10.0),
					context.getSamplingProbability())));
			
			context.addAgentToVisitedNodes(context.getCurrentAgent());
			context.tellMaster(EventType.NeihgborListRequest);
	
		} else if (Neighbors.class.isInstance(event)){
			context.getLoggingAdapter().debug("Received Neighbors list");
			neighbors = (Neighbors) event;
//			//asks talker to retrieve QoS of neighbors
			talker.tell(neighbors, context.getSelf());
			
		} else if (DMAS2ImmutableExplorationRepliesHolder.class.isInstance(event)) {
			context.getLoggingAdapter().debug("ImmutableExplorationRepliesHolder received");
			DMAS2ImmutableExplorationRepliesHolder replies = (DMAS2ImmutableExplorationRepliesHolder) event;
			
			for (ExplorationReplyWrapper erw: replies) {
				repliesHolder.add(erw);
			}
			
			if (repliesHolder.atLeastNbReplies(WAIT_FOR_NUMBER_REPLIES)) {
				ExplorationReplyWrapper ref = null;
				try {
					ref = repliesHolder.acoSelection();
				}
				catch (RuntimeException e) {
					context.getLoggingAdapter().error("Error in ACOSelection, " + repliesHolder.toString());
				}
				context.tellMaster(SimpleExplorationResult.getInstance(ref));
				return "Idle";
			} else {
				//Searches again. Here we could define another search strategy as well
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
	public void onEntry(Object event, DMAS2ExplorationAntContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Object event, DMAS2ExplorationAntContext context) {
		repliesHolder.clear();
	}

	@Override
	public String toString() {
		return "DMAS2ExploringState [talker=" + talker + ", neighbors="
				+ neighbors + ", repliesHolder=" + repliesHolder + "]";
	}

	
}
