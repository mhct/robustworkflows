package be.kuleuven.robustworkflows.infrastructure;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import be.kuleuven.robustworkflows.model.NodeAttributeValues;
import be.kuleuven.robustworkflows.model.NodeAttributes;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

public class InfrastructureStorage {

	private final static String EVENTS_COLLECTION = "model_events";
	private final static String FACTORY_AGENTS_COLLECTION = "model_factory_agents";
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	
	private DB db;

	public InfrastructureStorage(DB db) {
		this.db = db;
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
	public DBCursor getClientAgents() {
		return db.getCollection("actors").find(new BasicDBObject("agentType", NodeAttributeValues.Client)).sort(new BasicDBObject("actorName", 1));
	}

//	public DBObject getClientAgent(String i) {
//		DBCollection coll = db.getCollection("clientAgents");
//		DBObject obj = new BasicDBObject();
//		obj.put("actorName", i);
//		return coll.findOne(obj);
//	}

	public void persistActorAddress(String address, String nodeName,
			String agentType) {
		DBCollection coll = db.getCollection("actors");
		BasicDBObject obj = new BasicDBObject();
		obj.append("actorAddress", address);
		obj.append("actorName", Integer.valueOf(nodeName));
		obj.append("agentType", agentType);
		coll.insert(obj);		
	}
}
