package be.kuleuven.robustworkflows.model.ant;

import akka.actor.UntypedActor;
import be.kuleuven.robustworkflows.model.ModelStorage;
import be.kuleuven.robustworkflows.model.Workflow;
import be.kuleuven.robustworkflows.model.messages.QoSData;

public class ExplorationAnt extends UntypedActor {

	private final ModelStorage modelStorage;
	private final Workflow workflow;

	public ExplorationAnt(ModelStorage modelStorage, Workflow workflow) {
		this.modelStorage = modelStorage;
		this.workflow = workflow;
	}

	@Override
	public void onReceive(Object message) throws Exception {
		if (QoSData.class.isInstance(message)) {
			QoSData qos = (QoSData) message;
			
			
//			qos.serviceType();
		}
	}
	
	public static ExplorationAnt getInstance(ModelStorage modelStorage, Workflow workflow) {
		return new ExplorationAnt(modelStorage, workflow);
	}

}
