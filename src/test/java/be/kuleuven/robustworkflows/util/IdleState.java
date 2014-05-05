package be.kuleuven.robustworkflows.util;

import javax.annotation.Nullable;

public class IdleState implements State<Object, Object>{

	@Override
	public String name() {
		return "IdleState";
	}

	@Override
	@Nullable
	public Object handle(Object event, Object context) {
		System.out.println(name() + ": handling :" + event);
		return new ExploringState();
	}

	@Override
	public void onEntry(Object event, Object context) {
	}

	@Override
	public void onExit(Object event, Object context) {
	}

}
