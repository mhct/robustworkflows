package be.kuleuven.robustworkflows.model.ant;

import be.kuleuven.robustworkflows.model.messages.QoSData;
import akka.actor.UntypedActor;

public class ExplorationAnt extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if (QoSData.class.isInstance(message)) {
			QoSData qos = (QoSData) message;
			
//			qos.serviceType();
		}
	}
	
	public static ExplorationAnt getInstance() {
		return new ExplorationAnt();
	}

}
