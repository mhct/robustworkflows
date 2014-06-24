package be.kuleuven.robustworkflows.model.antactors.dmas;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior.messages.DMASExplorationResult;
import be.kuleuven.robustworkflows.model.messages.MutableWorkflowTask;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.model.messages.WorkflowTask;
import be.kuleuven.robustworkflows.util.State;
import be.kuleuven.robustworkflows.util.Utils;

/**
 * Explore until finding 5 candidate services of the required {@link ServiceType}.
 * 
 * @author mario
 *
 * === Inbound Messages ===
 * - '''Workflow'''
 * - '''Neighbors'''
 * 
 * === Outbound Messages ===
 * - '''NeighborListRequest'''
 * 
 */
public class DMASExplorationAntExploringState implements State<Object, DMASExplorationAntContext>{

	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	private static final int WAIT_FOR_NUMBER_REPLIES = 5;
	private Neighbors neighbors;
	private final DMASExplorationRepliesHolder repliesHolder;
	private Iterator<WorkflowTask> tasks;
	private Workflow workflow;
	private WorkflowTask currentTask;


	public DMASExplorationAntExploringState() {
		repliesHolder = DMASExplorationRepliesHolder.getInstance();	
	}
	
	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object handle(Object event, DMASExplorationAntContext context) {
		
		if (Workflow.class.isInstance(event)) {
			Utils.addExpirationTimer(context.getContext(), context.getExplorationTimeout(), EventType.ExploringStateTimeout);
			
			Workflow msg = (Workflow) event;
			workflow = msg.getMutableClone();
			tasks = workflow.iterator();
			
			currentTask = tasks.next();
			if (currentTask != null) {
				context.addAgentToVisitedNodes(context.getContext().parent());
				context.tellMaster(EventType.NeihgborListRequest);
			} else {
				context.getLoggingAdapter().error("WorkflowTask is null, perhaps Workflow has problem" + workflow);
			}
	
		} else if (Neighbors.class.isInstance(event)){
			neighbors = (Neighbors) event;

			ActorRef talker = context.getContext().actorOf(DMASTalkerAntActor.props(currentTask.getType(),
					(long) Math.ceil(context.getExplorationTimeout()/10.0)));
			talker.tell(neighbors, context.getSelf()); //asks QoS/pheromone of neighbors

		} else if (DMASImmutableExplorationRepliesHolder.class.isInstance(event)) {
			context.getLoggingAdapter().debug("ImmutableExplorationRepliesHolder received");
			DMASImmutableExplorationRepliesHolder replies = (DMASImmutableExplorationRepliesHolder) event;
			
			context.getContext().stop(context.getContext().sender()); //stop active talker
			
			for (ExplorationReplyWrapper erw: replies) {
				repliesHolder.add(erw);
			}
			
//			if (check for valid replies. if found replies, calculate according to ACO
//			and advance the workflow
//			if it didn't get replies for current task, calculate new neighbor according to ACO, 
//			and explore again, but for the same workflowtask
			if (repliesHolder.atLeastNbReplies(1)) {
				//calculate next agent HERE COMES THE ACO CALCULATIONS
				ActorRef selectedResourceAgent = repliesHolder.bestExplorationReply().getActor();
				((MutableWorkflowTask) currentTask).setAgent(selectedResourceAgent);
				
				if (tasks.hasNext()) {
					currentTask = tasks.next();
					context.addAgentToVisitedNodes(selectedResourceAgent);
					selectedResourceAgent.tell(EventType.NeihgborListRequest, context.getSelf());
				} else {
					// finished workflow
					repliesHolder.clear();
					context.tellMaster(DMASExplorationResult.getInstance(workflow));
					return "idle";
				}
			} else {
				// no replies, means either 1 - all timeouts / 2 - no RA with proper ServiceType
				// Thus, we can not advance the workflow
				ActorRef next = neighbors.getRandomNeighbor();
				
				if (next != null) {
					context.addAgentToVisitedNodes(next);
					next.tell(EventType.NeihgborListRequest, context.getSelf());
				} else {
					context.getLoggingAdapter().error("Neighborlist has NULL agent");
				}
			}
			
		} else if (EventType.ExploringStateTimeout.equals(event)) {
			context.getLoggingAdapter().debug("TIMEOUT :" + dtf.print(new DateTime()));
			repliesHolder.clear();
			context.tellMaster(DMASExplorationResult.getInstance(workflow));
			return "idle";
		} else {
			context.getLoggingAdapter().debug("Unhandled message: " + event);
			
		}
		
		return null;
	}

	
	@Override
	public void onEntry(Object event, DMASExplorationAntContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Object event, DMASExplorationAntContext context) {
		repliesHolder.clear();
	}

}
