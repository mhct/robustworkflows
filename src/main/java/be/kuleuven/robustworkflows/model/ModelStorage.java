package be.kuleuven.robustworkflows.model;

import java.util.Date;

import be.kuleuven.robustworkflows.model.clientagent.EventType;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;


/**
 * Storage for the model being executed
 * 
 * @category API
 * @author mario
 *
 */
public class ModelStorage {
	
	private DB db;
	
	public ModelStorage(DB db) {
		this.db = db;
	}

	/**
	 * Persists an event in the database
	 * 
	 * FIXME avoid this duplication of code in the two methods below
	 * @param event
	 */
	public void persistEvent(String event) {
		DBCollection coll = db.getCollection("robustworkflows");
		BasicDBObject obj = new BasicDBObject("current-time", new Date());
		obj.append("EventType", event);
		coll.insert(obj);
	}

	public void persistEvent(EventType eventType, String event) {
		DBCollection coll = db.getCollection("robustworkflows");
		BasicDBObject obj = new BasicDBObject("current-time", new Date());
		obj.append("EventType", eventType.toString());
		obj.append(eventType.toString(), event);
		coll.insert(obj);
	}
	
	public void persistEvent(BasicDBObject obj) {
		DBCollection coll = db.getCollection("robustworkflows");
		coll.insert(obj);
	}
	
}
