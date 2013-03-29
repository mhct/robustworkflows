package be.kuleuven.robustworkflows.model;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;


/**
 * Storage for the model being executed
 * 
 * @author mario
 *
 */
public class ModelStorage {
	
	private DB db;
	
	public ModelStorage(DB db) {
		this.db = db;
	}

	public void persistEvent(String event) {
		DBCollection coll = db.getCollection("robustworkflows");
		coll.insert(new BasicDBObject("Event", event));
	}
}
