package be.kuleuven.robustworkflows.util;

public class StateB implements State<Object, Object>{

	private String value;

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String handle(Object event, Object context) {
		if (String.class.isInstance(event)) {
			System.out.println("Value: " + event);
			this.value = String.valueOf(event);
//			return "another test... to check another state";
		} else if (Integer.class.isInstance(event)){
			System.out.println("Previous value: " + value + "Current event: " + event);
		}
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
