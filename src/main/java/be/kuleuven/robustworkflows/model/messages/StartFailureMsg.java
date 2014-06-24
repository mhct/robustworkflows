package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

public class StartFailureMsg implements Serializable {

	private static final long serialVersionUID = 20130926L;
	private long startTime;

	public StartFailureMsg(Long duration) {
		this.startTime = duration;
	}

	public Long getStartTime() {
		return startTime;
	}
	
	public static StartFailureMsg getInstance(long startTime) {
		return new StartFailureMsg(startTime);
	}
	
	@Override
	public String toString() {
		return "startTime: " + startTime;
	}
	
}
