/**
 * Administrative functions for the mongodb and Robustworkflows project
 */

dropall = function() {
        db.robustworkflows.drop();
        db.actors.drop();
        db.sorcerers.drop();
        db.clientAgents.drop();
        db.factory_agents.drop();
}

dropall();

