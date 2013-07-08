package be.kuleuven.robustworkflows.model;

import java.util.Date;
import java.util.List;

import be.kuleuven.robustworkflows.model.clientagent.EventType;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;


/**
 * Storage for the model being executed
 * 
 * @category API
 * @author mario
 *
 */
public class ModelStorage {
	private final static String EVENTS_COLLECTION = "model_events";
	private final static String FACTORY_AGENTS_COLLECTION = "model_factory_agents";
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
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("current-time", new Date());
		obj.append("EventType", event);
		coll.insert(obj);
	}

	public void persistEvent(EventType eventType, String event) {
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("current-time", new Date());
		obj.append("EventType", eventType.toString());
		obj.append(eventType.toString(), event);
		coll.insert(obj);
	}
	
	public void persistEvent(BasicDBObject obj) {
		obj.append("current-time", new Date());
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		coll.insert(obj);
	}

	public void registerFactoryAgent(String path, ServiceType serviceType) {
		DBCollection coll = db.getCollection(FACTORY_AGENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("current-time", new Date());
		obj.append("FactoryAgentPath", path);
		obj.append("ServiceType", serviceType.toString());
		coll.insert(obj);
	}

	public List<String> getFactoryAgents(ServiceType st) {
		List<String> agentPaths = Lists.newArrayList();
		
		DBCollection coll = db.getCollection(FACTORY_AGENTS_COLLECTION);
		BasicDBObject query = new BasicDBObject("ServiceType", st.toString());
		
		DBCursor cursor = coll.find(query);
		while (cursor.hasNext()) {
			agentPaths.add((String)cursor.next().get("FactoryAgentPath"));
		}
		
		return agentPaths;
	}

	public void unRegisterFactoryAgent(String path) {
		DBCollection coll = db.getCollection(FACTORY_AGENTS_COLLECTION);
		BasicDBObject query = new BasicDBObject("FactoryAgentPath", path);
		
		coll.remove(query);
	}
	
}
