package be.kuleuven.robustworkflows.model.messages;

import java.io.Serializable;

public class StartExperimentRun implements Serializable {

	private static final long serialVersionUID = 20130926L;
	private String run;

	public StartExperimentRun(String run) {
		this.run = run;
	}

	public String getRun() {
		return run;
	}
	
	public static StartExperimentRun getInstance(String run) {
		return new StartExperimentRun(run);
	}
	
	@Override
	public String toString() {
		return "run: " + run;
	}
	
}
