package be.kuleuven.robustworkflows.model.antactors.dmas;

import javax.annotation.Nullable;

import be.kuleuven.robustworkflows.util.State;

public class DMASIdleState implements State<Object, DMASExplorationAntContext>{

	@Override
	public String name() {
		return "IdleState";
	}

	@Override
	@Nullable
	public Object handle(Object event, DMASExplorationAntContext context) {
		
//		StartExperimentRun msg = (StartExperimentRun) message;
//		modelStorage.addField("run", msg.getRun());
//		replies = Lists.newArrayList();
		System.out.println(name() + ": handling :" + event);
		return null;
	}

	@Override
	public void onEntry(Object event, DMASExplorationAntContext context) {
	}

	@Override
	public void onExit(Object event, DMASExplorationAntContext context) {
	}

}
