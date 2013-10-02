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
	
	public void persistActorAddress(String address, String actorName) {
		DBCollection coll = db.getCollection("actors");
		BasicDBObject obj = new BasicDBObject();
		obj.append("actorAddress", address);
		obj.append("actorName", actorName);
		coll.insert(obj);
	}
	
	public void persistSorcererAddress(String address) {
		DBCollection coll = db.getCollection("sorcerers");
		coll.insert(new BasicDBObject("sorcererPath", address));
	}
	
	public DBCollection getSorcerers() {
		return db.getCollection("sorcerers");
	}
	
	//TODO should return collection here, or a cursor?
	public DBCollection getActors() {
		return db.getCollection("actors");
	}

	//@TODO move this to ModelStorage
	public DBCollection getClientAgent() {
		return db.getCollection("clientAgents");
	}

	public void persistClientAgentAddress(String address, String actorName) {
		BasicDBObject obj = new BasicDBObject();
		obj.append("actorAddress", address);
		obj.append("actorName", actorName);
		db.getCollection("clientAgents").insert(obj);
	}

	public void persistFactoryAgentAddress(String address) {
		DBCollection coll = db.getCollection("factory_agents");
		DBObject obj = new BasicDBObject();
		obj.put("Current Time", System.currentTimeMillis());
		obj.put("ActorName", address);
		
		coll.insert(obj);
	}

	public DBObject getClientAgent(String i) {
		DBCollection coll = db.getCollection("clientAgents");
		DBObject obj = new BasicDBObject();
		obj.put("actorName", i);
		return coll.findOne(obj);
	}
}
