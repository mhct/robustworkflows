package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

public class Compose implements Serializable {

	private static final long serialVersionUID = 20130926L;
	private String run;

	public Compose(String run) {
		this.run = run;
	}

	public String getRun() {
		return run;
	}
	
	public static Compose getInstance(String run) {
		return new Compose(run);
	}
}
