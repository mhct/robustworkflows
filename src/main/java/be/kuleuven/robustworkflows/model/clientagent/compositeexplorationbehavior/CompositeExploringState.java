package be.kuleuven.robustworkflows.model.clientagent.compositeexplorationbehavior;

import java.util.List;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.messages.ExplorationResult;

import com.google.common.collect.Lists;
import com.mongodb.DBObject;

/**
 * === InboundMessages ===
 * - '''ExplorationResult''' Results from CompositeExplorationAnts 
 * - 
 * @author mario
 *
 */
public class CompositeExploringState extends ClientAgentState {
	
	private final String EXPLORING_STATE_TIMEOUT = "ExploringStateTimeout";
	private List<ExplorationResult> replies;
	private int expirationTimer = 1;
	
	public CompositeExploringState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	public void run() {
//		persistEvent("ExploringState: " + RUN);
		replies = Lists.newArrayList();
		if (getClientAgentProxy().getAntAPI().explorationAnts() == 0) {
			getClientAgentProxy().getAntAPI().createExplorationAnt(new ExplorationAntParameter(getClientAgentProxy().self(), 
					getClientAgentProxy().getModelStorage(), 
					getClientAgentProxy().getWorkflow(), 
					getClientAgentProxy().getAttributes().getAntExplorationTimeout(), 
					getClientAgentProxy().getAttributes().getAntExplorationSamplingProbability())
					);
		}
		
		//TODO create new exploration ANTS NOW..? at at ClientAgent creation
		addExpirationTimer(getClientAgentProxy().getAttributes().getExplorationStateTimeout(), EXPLORING_STATE_TIMEOUT);
	}

	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (RUN.equals(message)) {
			run();
		} else if (EXPLORING_STATE_TIMEOUT.equals(message)) {
			//TODO if there are no replies.... go back to another state, instead of SelectingComponentServices.
//			persistEvent(EXPLORING_STATE_TIMEOUT);
			if (replies.size() == 0) {
				getClientAgentProxy().addExpirationTimer(expirationTimer, RUN);
				expirationTimer += expirationTimer;
			} else {
//				setState(SelectingComponentServices.getInstance(getClientAgentProxy(), replies));
			}
			
		} else if (ExplorationResult.class.isInstance(message))  {
			ExplorationResult msg = (ExplorationResult) message;
			
			if (msg.getWorkflow().fulfilled()) {
				replies.add(msg);
			}
			
//			persistEvent(debugExplorationResult(msg));
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
		
		//TODO add return type and with TRUE or FALSE for possible to handle of not.. the message think about this. 
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new CompositeExploringState(clientAgentProxy);
	}
	
	private DBObject debugExplorationResult(ExplorationResult msg) {
		DBObject obj = msg.toDBObject();
		obj.put("CLIENT_AGENT", getClientAgentProxy().self().path().name());
		
		return obj;
	}
}
