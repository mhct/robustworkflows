package be.kuleuven.robustworkflows.model.antactors.reactive;

import javax.annotation.Nullable;

import be.kuleuven.robustworkflows.model.messages.StartExperimentRun;
import be.kuleuven.robustworkflows.util.State;

public class IdleState implements State<Object, ExplorationAntContext>{

	@Override
	public String name() {
		return "IdleState";
	}

	@Override
	@Nullable
	public Object handle(Object event, ExplorationAntContext context) {
		
		if (StartExperimentRun.class.isInstance(event)) {
			System.out.println("ReactiveAnt received " + event);
		}
//		StartExperimentRun msg = (StartExperimentRun) message;
//		modelStorage.addField("run", msg.getRun());
//		replies = Lists.newArrayList();
		System.out.println(name() + ": handling :" + event);
		return null;
	}

	@Override
	public void onEntry(Object event, ExplorationAntContext context) {
	}

	@Override
	public void onExit(Object event, ExplorationAntContext context) {
	}

}
