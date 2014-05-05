package be.kuleuven.robustworkflows.util;

import javax.annotation.Nullable;

public class ExploringState implements State<Object, Object>{

	@Override
	public String name() {
		return "ExploringState";
	}

	@Override
	@Nullable
	public Object handle(Object event, Object context) {
		System.out.println(name() + " handling: " + event);
		return null;
	}

	@Override
	public void onEntry(Object event, Object context) {
		System.out.println("entered" + name());
	}

	@Override
	public void onExit(Object event, Object context) {
	}

}
