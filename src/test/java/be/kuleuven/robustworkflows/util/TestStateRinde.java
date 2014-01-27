package be.kuleuven.robustworkflows.util;

import javax.annotation.Nullable;

import be.kuleuven.robustworkflows.util.StateMachineTest.Bla;

public class TestStateRinde implements State<Bla, Object>{

	@Override
	public String name() {
		return "TestStateRinde";
	}

	@Override
	@Nullable
	public Bla handle(Bla event, Object context) {
		return null;
	}

	@Override
	public void onEntry(Bla event, Object context) {
	}

	@Override
	public void onExit(Bla event, Object context) {
	}

}
