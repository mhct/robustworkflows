package be.kuleuven.robustworkflows.util;

public class StateA implements State<Object, Object> {

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(Object event, Object context) {
		System.out.println("StateA, handling another event: " + event);
		return null;
	}

	@Override
	public void onEntry(Object event, Object context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit(Object event, Object context) {
		// TODO Auto-generated method stub
		
	}

}
