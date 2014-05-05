package be.kuleuven.robustworkflows.model.antactors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.ant.messages.ExplorationReplyWrapper;
import be.kuleuven.robustworkflows.model.ant.messages.ExploreService;
import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.EmptyExplorationResult;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.messages.SimpleExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
import be.kuleuven.robustworkflows.model.messages.Workflow;
import be.kuleuven.robustworkflows.util.StateMachine;
import be.kuleuven.robustworkflows.util.Utils;
import be.kuleuven.robustworkflows.util.StateMachine.StateMachineBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Explores the network looking for only one service
 * 
 * @author marioct
 *
 * === Inbound messages ===
 * - ''' ExploreService ''' 
 * - ''' ExploringStateTimeout '''
 * - ''' ExplorationFinished '''
 * - ''' Compose ''' tell the explorationAnt to mark all its events as bellonging to another RUN of the emulation
 * 
 * === Outbound messages ===
 * - ''' SimpleExplorationResult '''
 * 
 */
public class ExplorationAntActor extends UntypedActor {

	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final ActorRef master;
	
	private Integer linearID = 0;
	private List<ExplorationReplyWrapper> replies = new ArrayList<ExplorationReplyWrapper>();
	private Set<ActorRef> pathFollowed;
	private long explorationTimeout;
	
	private int minimum_nbRplies = 1;
	
	private ServiceType serviceType;

	private ActorRef currentAgent; //Current Node that the exploration ant is "seating"

	private Neighbors neighbors;

	private int askedQos;

	private int receivedReplies;

	private double samplingProbability;

	private ActorRef talker;

	private boolean ignoreTimeout = false;
	
	public ExplorationAntActor(ActorRef master, long explorationTimeout, double samplingProbability) {
		this.master = master;
		this.currentAgent = master;
		this.explorationTimeout = explorationTimeout;
		this.samplingProbability = samplingProbability;
		this.pathFollowed = Sets.newHashSet();
		
		askedQos = 0;
		receivedReplies = 0;
		
//		StateMachineBuilder<Object, Object> builder = StateMachine.create(idle);
//		builder.addTransition(idle, "ExploreService", exploringState);
//		
//		StateMachine<Object, Object> fsm = builder.build();
//		fsm.handle("Explore", null);
	}

	@Override
	public void postStop() {
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (StartExperimentRun.class.isInstance(message)) {
//			StartExperimentRun msg = (StartExperimentRun) message;
//			modelStorage.addField("run", msg.getRun());
			replies = Lists.newArrayList();
			
		} else if (ExploreService.class.isInstance(message)) {
			Utils.addExpirationTimer(context(), explorationTimeout, EventType.ExploringStateTimeout);
			ExploreService msg = (ExploreService) message;
			
			serviceType = msg.getServiceType();
			talker = getContext().actorOf(TalkerAntActor.props(msg.getServiceType(), (long) Math.ceil(explorationTimeout/4.0), samplingProbability));
			pathFollowed.add(currentAgent);
			talker.tell(currentAgent, self());
		
		} else if (ExplorationReplyWrapper.class.isInstance(message)) {
			ExplorationReplyWrapper wrapper = (ExplorationReplyWrapper) message;
			if (wrapper.getReply().isPossible()) {
				//jumps to the agent which has the best reply
				currentAgent = wrapper.getActor();
				master.tell(SimpleExplorationResult.getInstance((ExplorationReplyWrapper)message), self());
			} else {
				master.tell(new EmptyExplorationResult(), self());
			}
			ignoreTimeout = true;
			
		} else if (!ignoreTimeout  && ( EventType.ExploringStateTimeout.equals(message) || EventType.ExplorationFinished.equals(message))) {
		
////			FIXME modelStorage.persistEvent(ExplorationAntEvents.timeout(master.path().name()));
//			log.debug("nbReplies: " + replies.size());
//			
//			SimpleExplorationResult bla = bestReply();
//			replies.clear();
//			
//			if (bla == null) {
//				log.info("ExplorationAnt: Didn't receive any replies");
				master.tell(new EmptyExplorationResult(), self());
//			} else {
//				master.tell(bla, self());
//			}
		}
	}
	
//	private void shouldExploreAgain() {
//		if (receivedReplies == askedQos && replies.size() < minimum_nbRplies) {
//			exploreAgain();
//		}
//	}
	
//	private void exploreAgain() {
//		// selects a new agent from the neighbors it knows
//		receivedReplies = 0;
//		askedQos = 0;
//		boolean continueSearch = true;
//		
//		if (neighbors.isEmpty()) {
//			log.info("nbNeighbors = 0");
//			return;
//		}
//		
//		ActorRef tentativeAgent = null;
//		while (continueSearch) {
//			tentativeAgent = neighbors.getRandomNeighbor();
//			if (pathFollowed.contains(tentativeAgent)) {
//				continueSearch = true;
//			} else {
//				break;
//			}
//		}
//		if (tentativeAgent == null) {
//			log.error("nbNeighbors = 0");
//		}
//		
//		currentAgent = tentativeAgent;
//		pathFollowed.add(currentAgent);
//		currentAgent.tell(EventType.NeihgborListRequest, self());
//	}
	
//	private SimpleExplorationResult bestReply() {
//		Collections.sort(replies, new Comparator<ExplorationReplyWrapper>() {
//
//			@Override
//			public int compare(ExplorationReplyWrapper obj1, ExplorationReplyWrapper obj2) {
//				final long o1 = obj1.getReply().getComputationTime();
//				final long o2 = obj2.getReply().getComputationTime();
//				
//				if (o1 < o2) {
//					return -1;
//				} else if (o1 > o2) {
//					return 1;
//				}
//					
//				return 0;
//			}
//			
//		});
//		
//		if (replies.size() >= 1) {
//			return SimpleExplorationResult.getInstance(replies.get(0).getActor(), replies.get(0).getReply().getComputationTime(), replies.get(0).getReply().getRequestExploration().getServiceType());
//		} else {
//			//FIXME modelStorage.persistEvent(ExplorationAntEvents.noReplies(master.path().name()));
//		}
//		
//		return null;
//	}
	
	public static UntypedActor getInstance(ExplorationAntParameter parameterObject) {
		return new ExplorationAntActor(parameterObject.getMaster(),
				parameterObject.getExplorationTimeout(),
				parameterObject.getSamplingProbability());
	}
}
