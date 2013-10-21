package be.kuleuven.robustworkflows.model.clientagent.simpleexplorationbehaviour;

import akka.actor.ActorRef;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentProxy;
import be.kuleuven.robustworkflows.model.clientagent.ClientAgentState;
import be.kuleuven.robustworkflows.model.clientagent.ExplorationAntParameter;
import be.kuleuven.robustworkflows.model.events.ComposeEvent;
import be.kuleuven.robustworkflows.model.events.WaitingTaskStateEvents;
import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;

/**
 * === Inbound Messages ===
 * - '''StartExperimentRun'''
 * - '''Compose'''
 * 
 * === Outbound Messages ===
 * - '''StartExperimentRun''' - send this message via the AntAPI
 * 
 * @author mario
 *
 */
public class SimpleWaitingTaskState extends ClientAgentState {

//	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private SimpleWaitingTaskState(ClientAgentProxy clientAgentProxy) {
		super(clientAgentProxy);
	}

	@Override
	public void run() {
		persistEvent(WaitingTaskStateEvents.instance());
	}
	@Override
	public void onReceive(Object message, ActorRef actorRef) throws Exception {
		if (StartExperimentRun.class.isInstance(message)) {
			StartExperimentRun msg = (StartExperimentRun) message;
			
			//FIXME create exploration ants here.. check a better place for this....
			if (getClientAgentProxy().getAntAPI().explorationAnts() != 1) {

				getClientAgentProxy().getAntAPI().createExplorationAnt(new ExplorationAntParameter(getClientAgentProxy().self(), 
						getClientAgentProxy().getModelStorage(), 
						getClientAgentProxy().getWorkflow(), 
						getClientAgentProxy().getAttributes().getAntExplorationTimeout(), 
						getClientAgentProxy().getAttributes().getAntExplorationSamplingProbability())
						);
			}
			
			getClientAgentProxy().getModelStorage().addField("run", msg.getRun());
			getClientAgentProxy().getAntAPI().tellAll(msg);
			
		} else if (COMPOSE.equals(message)) {
			persistEvent(ComposeEvent.getInstance());
			ClientAgentState bla = RunningCompositionState.getInstance(getClientAgentProxy());
			getClientAgentProxy().setHackingState(bla);
			setState(bla);
			
		} else {
			getClientAgentProxy().unhandledMessage(message);
		}
	}

	public static ClientAgentState getInstance(ClientAgentProxy clientAgentProxy) {
		return new SimpleWaitingTaskState(clientAgentProxy);
	}
}
