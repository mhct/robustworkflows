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

experiment_data = function(dbName) {
    use dbName;
    sorcerers = db.sorcerers.find().size();
    clients = db.clientAgents.find().size();
    factories = db.factry_agents.find().size();
    
    print("Sorcers: " + sorcerers);
    print("ClientAgents: " + clients);
    print("Factories: " + factories);
}
