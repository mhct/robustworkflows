package be.kuleuven.robustworkflows.infrastructure;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class InfrastructureStorage {

	private DB db;

	public InfrastructureStorage(DB db) {
		this.db = db;
	}
	
	public void persistActorAddress(String address) {
		DBCollection coll = db.getCollection("actors");
		coll.insert(new BasicDBObject("actorAddress", address));
	}
	
	public void persistSorcererAddress(String address) {
		DBCollection coll = db.getCollection("sorcerers");
		coll.insert(new BasicDBObject("sorcererPath", address));
	}
	
	public DBCollection getSorcerers() {
		return db.getCollection("sorcerers");
	}
	
	public DBCollection getActors() {
		return db.getCollection("actors");
	}

	public void persistClientAgentAddress(String address) {
		db.getCollection("clientAgents").insert(new BasicDBObject("address", address));
	}

	public void persistFactoryAgentAddress(String address) {
		DBCollection coll = db.getCollection("factory_agents");
		DBObject obj = new BasicDBObject();
		obj.put("Current Time", System.currentTimeMillis());
		obj.put("ActorName", address);
		
		coll.insert(obj);
	}
}
