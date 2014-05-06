package be.kuleuven.robustworkflows.model.antactors;

import javax.annotation.Nullable;

import be.kuleuven.robustworkflows.util.State;

public class IdleState implements State<Object, ExplorationAntContext>{

	@Override
	public String name() {
		return "IdleState";
	}

	@Override
	@Nullable
	public Object handle(Object event, ExplorationAntContext context) {
		
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
