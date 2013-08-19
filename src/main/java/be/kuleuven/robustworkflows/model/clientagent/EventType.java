package be.kuleuven.robustworkflows.model.clientagent;

public enum EventType {
	SERVICE_REQUEST_SUMMARY,
	SERVICE_COMPOSITION_SUMMARY, //FIXME THIS IS a type of document. Not a type of event for the actors... I have to clearly separate Events from actors and Documents to be persisted.
	RUN, 
	NeihgborListRequest, 
	ExploringStateTimeout, 
	ExplorationFinished, 
	ExplorationResult, 
	ExplorationReply;
}
