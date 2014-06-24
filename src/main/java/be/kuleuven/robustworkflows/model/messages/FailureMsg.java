package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

public class FailureMsg implements Serializable {

	private static final long serialVersionUID = 20130926L;
	private long duration;

	public FailureMsg(Long duration) {
		this.duration = duration;
	}

	public Long getDuration() {
		return duration;
	}
	
	public static FailureMsg getInstance(long duration) {
		return new FailureMsg(duration);
	}
	
	@Override
	public String toString() {
		return "duration: " + duration;
	}
	
}
