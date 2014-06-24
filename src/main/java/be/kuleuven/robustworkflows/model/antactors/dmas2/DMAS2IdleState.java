package be.kuleuven.robustworkflows.model.antactors.dmas2;

import javax.annotation.Nullable;

import be.kuleuven.robustworkflows.util.State;

public class DMAS2IdleState implements State<Object, DMAS2ExplorationAntContext>{

	@Override
	public String name() {
		return "IdleState";
	}

	@Override
	@Nullable
	public Object handle(Object event, DMAS2ExplorationAntContext context) {
		
//		StartExperimentRun msg = (StartExperimentRun) message;
//		modelStorage.addField("run", msg.getRun());
//		replies = Lists.newArrayList();
		System.out.println(name() + ": handling :" + event);
		return null;
	}

	@Override
	public void onEntry(Object event, DMAS2ExplorationAntContext context) {
	}

	@Override
	public void onExit(Object event, DMAS2ExplorationAntContext context) {
	}

}
