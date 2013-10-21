/**
 * Administrative functions for the mongodb and Robustworkflows project
 */

dropall = function() {
        db.actors.drop();
        db.sorcerers.drop();
        db.clientAgents.drop();
        db.factory_agents.drop();

        db.model_events.drop();
        db.model_factory_agents.drop();
        
        print("Cleaned db");
}



edata = function() {
    sorcerers = db.sorcerers.find().size();
    clients = db.clientAgents.find().size();
    factories = db.factory_agents.find().size();
    
    print("Sorcers: " + sorcerers);
    print("ClientAgents: " + clients);
    print("Factories: " + factories);
}

client = function(id) {
	db.model_events

}

dropDBS = function() {
	dbs = db.getMongo().getDBNames()
	for (i in dbs) { 
		db = db.getMongo().getDB( dbs[i] );     
		print( "dropping db " + db.getName() );     
		db.dropDatabase(); 
	}
}

summary = function(run_id) {
	query = {EventType : "SERVICE_COMPOSITION_SUMMARY", run: run_id.toString()};
	print("Finished Compositions:\t" + db.model_events.find(query).size());
	
	query_events = {run: run_id.toString()};
	print("# Events:\t" + db.model_events.find(query_events).size());

	broken_compositions = {EventType: "EXPLORATION_ANT_NO_REPLIES", run: run_id.toString()}
	print("# Broken Compositions:\t" + db.model_events.find(broken_compositions).size());
	
	query_req = {EventType: "ExplorationRequest", run: run_id.toString()};
	print("# Exp.Requests:\t" + db.model_events.find(query_req).size());

	query_rep = {EventType: "ExplorationReply", run: run_id.toString()};
	print("# Exp. Replies:\t" + db.model_events.find(query_rep).size());

	query = {EventType: "ServiceRequest", run: run_id.toString()};
	print("# Service Req.:\t" + db.model_events.find(query).size());

	query = {EventType: "SERVICE_REQUEST_SUMMARY", run: run_id.toString()};
	print("# Service Req. Summary:\t" + db.model_events.find(query).size());
	
	query = {EventType: "TIME_TO_WORK_FOR_REQUEST_FINISHED", run: run_id.toString()};
	print("# Worked Req.:\t" + db.model_events.find(query).size());

	query = {EventType: "Compose", run: run_id.toString()};
	print("# Compositions:\t" + db.model_events.find(query).size());
	
}