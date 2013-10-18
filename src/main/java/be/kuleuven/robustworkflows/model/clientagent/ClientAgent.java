package be.kuleuven.robustworkflows.model.clientagent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import be.kuleuven.robustworkflows.infrastructure.InfrastructureStorage;
import be.kuleuven.robustworkflows.model.AgentAttributes;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ant.AntAPI;
import be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour.RequestExecutionData;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.Workflow;

import com.google.common.collect.Lists;
import com.mongodb.DB;

/**
 * A ClientAgent represents a composite service. 
 * 
 * A ClientAgent tries to find suitable component services to participate in a service composition. A service composition is, in turn,
 * defined by a {@link Workflow}.
 * 
 * A ClientAgent behavior is mainly defined by concrete implementations of {@link ClientAgentState}. The behaviors are instantiated
 * using factories of {@ ExplorationBehaviorFactory}, such as {@link SimpleExplorationFactory} and {@link ExplorationBehaviorFactory}. 
 * 
 * @author mario
 *
 */
public class ClientAgent extends UntypedActor implements ClientAgentProxy {
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private final List<ActorRef> neighbors;
	private ClientAgentState currentState;
	private InfrastructureStorage storage;
	private ModelStorage modelStorage;
	private AntAPI antApi;
	private Workflow workflow;
	private AgentAttributes attributes;
	private List<RequestExecutionData> requestsExecutionData;
	private ClientAgentState hackState;
	
	public ClientAgent(DB db, List<ActorRef> neighbors, AgentAttributes attributes, ExplorationBehaviorFactory behaviorFactory) {
		log.info("C L I E N T started\nBehavior Factory: " + behaviorFactory.getClass().getName());
		
		this.neighbors = neighbors;
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = ModelStorage.getInstance(db);
		this.currentState = behaviorFactory.createWaitingState((ClientAgentProxy) this);
		this.antApi = AntAPI.getInstance(behaviorFactory, self(), context());
		this.attributes = attributes;
		this.workflow = Workflow.getLinear1();
		requestsExecutionData = new ArrayList<RequestExecutionData>();
	}
	
	@Override
	public void preStart() {
		log.debug("----->>>>" + self().path().toStringWithAddress(getContext().provider().getDefaultAddress()) + "<<<<<<<<-----");
		storage.persistClientAgentAddress(self().path().toStringWithAddress(getContext().provider().getDefaultAddress()), self().path().name());
	}
	
	@Override
	public void postStop() {
		log.debug("ClientAgent stoped");
	}
	
	@Override
	public void onReceive(Object message) throws Exception {

		if(ActorRef.class.isInstance(message)) {
			log.debug("\n\n\nAdding neighbor to neighborlist" + message);
			neighbors.add((ActorRef) message);
			
		} else if (EventType.NeihgborListRequest.equals(message)){
			log.debug(self() + " got " + message);
			sender().tell(getNeighbors(), self());
			
		} else if ("expirationTimer".equals(message)) {
			log.debug(message + ". from " + sender());
			addExpirationTimer(10, "test");
			
		} else if ("test".equals(message)) {
			log.debug(message.toString());
			
		}
		else {
			currentState.onReceive(message, sender());
		}
	}
	
	public void unhandledMessage(Object message) {
		unhandled(message);
	}

	private Neighbors getNeighbors() {
		return Neighbors.getInstance(Lists.newArrayList(neighbors));
	}
	
	public void setState(ClientAgentState state) {
		this.currentState = state;
		currentState.run();
	}
	
	protected void addNeighbor(ActorRef actor) {
		neighbors.add(actor);
	}

	@Override
	public ModelStorage getModelStorage() {
		return modelStorage;
	}

	@Override
	public void addExpirationTimer(long time, final String message) {
		final ActorRef selfReference = self();
		
		context().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, selfReference);
					}
		}, context().system().dispatcher());
	}

	/**
	 * Selects the best (lowest totalComputationTime) ExplorationResult with the lowest QoS
	 * 
	 */
	@Override
	public ExplorationResult evaluateComposition(List<ExplorationResult> replies) {
		
		Collections.sort(replies, new Comparator<ExplorationResult>() {

			@Override
			public int compare(ExplorationResult obj1, ExplorationResult obj2) {
				final long o1 = obj1.totalComputationTime();
				final long o2 = obj2.totalComputationTime();
				
				if (o1 < o2) {
					return -1;
				} else if (o1 > o2) {
					return 1;
				}
					
				return 0;
			}
			
		});
		
		assert replies.get(0) != null;
		return replies.get(0);
	}

	@Override
	public Workflow getWorkflow() {
		return workflow;
	}

	@Override
	public AntAPI getAntAPI() {
		return antApi;
	}
	
	@Override
	public AgentAttributes getAttributes() {
		return attributes;
	}

	@Override
	public String clientAgentName() {
		return self().path().name();
	}

	@Override
	public String clientAgentCompleteName() {
		return self().path().address().toString();
	}

	@Override
	public List<RequestExecutionData> getRequestsData() {
		return new ArrayList<RequestExecutionData>(requestsExecutionData);
	}

	@Override
	public void addRequestsExecutionData(RequestExecutionData requestData) {
		requestsExecutionData.add(requestData);
	}

	@Override
	public void clearRequestsData() {
		requestsExecutionData.clear();
		assert requestsExecutionData.isEmpty() == true;
	}

	@Override
	public void setHackingState(ClientAgentState bla) {
		hackState = bla;
	}

	@Override
	public ClientAgentState getHackingState() {
		return hackState;
	}
	
}
