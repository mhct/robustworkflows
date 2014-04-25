package be.kuleuven.robustworkflows.model.clientagent;

public enum EventType {
	SERVICE_REQUEST_SUMMARY,
	RUN, 
	NeihgborListRequest, 
	ExplorationFinished, 
	ExplorationResult, 
	ExplorationReply, ExploringStateTimeout, AskQoSTimeout;
}
