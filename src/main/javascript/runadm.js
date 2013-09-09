/**
 * Administrative functions for the mongodb and Robustworkflows project
 */

dropall = function() {
        db.robustworkflows.drop();
        db.actors.drop();
        db.sorcerers.drop();
        db.clientAgents.drop();
        db.factory_agents.drop();
        db.model_events.drop();
        db.model_factory_agents.drop();
        
        Date();
}

dropall();

