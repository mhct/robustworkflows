package be.kuleuven.robustworkflows.model.clientagent;

public enum EventType {
	ServiceRequestSummary,
	TotalTimeToServeRequest, 
	ExpectedTimeToServeRequest,
	RUN, 
	NeihgborListRequest, 
	ExploringStateTimeout, 
	ExplorationFinished;
}
