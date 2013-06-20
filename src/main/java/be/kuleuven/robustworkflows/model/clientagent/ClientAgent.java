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
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.ServiceType;
import be.kuleuven.robustworkflows.model.Workflow;
import be.kuleuven.robustworkflows.model.ant.AntAPI;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;
import be.kuleuven.robustworkflows.model.messages.Neighbors;
import be.kuleuven.robustworkflows.model.messages.ServiceRequestExploration;

import com.google.common.collect.Lists;
import com.mongodb.DB;

/**
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
	
	public ClientAgent(DB db, ArrayList<ActorRef> arrayList) {
		log.info("C L I E N T started");
		
		this.neighbors = arrayList;
		this.storage = new InfrastructureStorage(db);
		this.modelStorage = new ModelStorage(db);
		this.currentState = WaitingTaskState.getInstance((ClientAgentProxy) this);
		this.antApi = AntAPI.getInstance(self(), context(), modelStorage);
		this.workflow = Workflow.getLinear1();
	}
	
	@Override
	public void preStart() {
		log.debug("----->>>>" + self().path().toStringWithAddress(getContext().provider().getDefaultAddress()) + "<<<<<<<<-----");
		storage.persistClientAgentAddress(self().path().toStringWithAddress(getContext().provider().getDefaultAddress()));
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		//Add reference to current actor
		if(ActorRef.class.isInstance(message)) {
			log.debug("\n\n\nAdding neighbor to neighborlist" + message);
			neighbors.add((ActorRef) message);
		} else if (EventType.NeihgborListRequest.equals(message)){
			log.debug(self() + " got " + message);
			sender().tell(getNeighbors(), self());
		} else {
			log.debug("\n\n\nClientAgent, received Message: " + message + "$ from " + sender());
			currentState.onReceive(message, sender());
		}
	}
	
	public void unhandledMessage(Object message) {
		unhandled(message);
	}

	/**
	 * Broadcasts a message to all neighbors
	 * 
	 * @param msg
	 */
//	public void broadcastToNeighbors(Object msg) {
//		for (ActorRef neighbor: neighbors) {
//			neighbor.tell(msg, self());
//		}
//	}
	
	private Neighbors getNeighbors() {
		return Neighbors.getInstance(Lists.newArrayList(neighbors));
	}
	
	public void setState(ClientAgentState state) {
		this.currentState = state;
//		this.currentState.run();
		addExpirationTimer(1, ClientAgentState.RUN); //this way I avoid having infinite calls for changing states after state
	}
	
	protected void addNeighbor(ActorRef actor) {
		neighbors.add(actor);
	}

	@Override
	public ModelStorage getModelStorage() {
		log.debug("Getting Model Storage");
		return modelStorage;
	}

	@Override
	public void addExpirationTimer(long time, final String message) {
//		system.scheduler().scheduleOnce(Duration.create(10, TimeUnit.SECONDS), getClientAgent(), system.dispatcher(), null);
		context().system().scheduler().scheduleOnce(Duration.create(time, TimeUnit.MILLISECONDS), 
				new Runnable() {
					@Override
					public void run() {
						self().tell(message, null);
					}
		}, context().system().dispatcher());
	}

	
	//TODO add abstraction to represent workflow
	@Override
	public ServiceRequestExploration getServiceRequestExploration() {
		return ServiceRequestExploration.getInstance(ServiceType.A, 10, self());
	}

	public AntAPI getAntAPI() {
		return antApi;
	}
	
	/**
	 * Selects the best (lowest totalComputationTime) ExplorationResult with the lowest QoS
	 */
	@Override
	public ExplorationResult evaluateComposition(List<ExplorationResult> replies) {
		
		Collections.sort(replies, new Comparator<ExplorationResult>() {

			@Override
			public int compare(ExplorationResult o1, ExplorationResult o2) {
				final long o1Time = o1.totalComputationTime();
				final long o2Time = o2.totalComputationTime();
				
				if (o1Time < o2Time) {
					return -1;
				} else if (o2Time > o2Time) {
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
	
}
