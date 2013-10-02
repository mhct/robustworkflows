package be.kuleuven.robustworkflows.model;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import be.kuleuven.robustworkflows.model.clientagent.EventType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


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
	private final static DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH_mm_ss_SSS");
	
	private final DB db;
	private Map<String, Object> extraFields;
	
	
	private ModelStorage(DB db) {
		this.db = db;
		extraFields = Maps.newHashMap();
		System.out.println("mark");
	}

	public void addField(String key, Object value) {
		extraFields.put(key, value);
	}
	
	/**
	 * This method adds extra fields, needed for running multiple experiments, to the persisted object.
	 * 
	 * @param coll
	 * @param obj
	 */
	private void insert(DBCollection coll, DBObject obj) {
		String val = (String) obj.get("EventType");
		if("SERVICE_REQUEST_SUMMARY".equals(val)) {
			System.out.println("DO");
		}
		for (Map.Entry<String, Object> e: extraFields.entrySet()) {
			obj.put(e.getKey(), e.getValue());
			
		}
		
		coll.insert(obj);
	}
	
	/**
	 * Persists an event in the database
	 * 
	 * FIXME avoid this duplication of code in the two methods below
	 * @param event
	 */
	public void persistEvent(String event) {
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("time_block", dtf.print(new DateTime()));
		obj.append("EventType", event);
		insert(coll, obj);
	}

	public void persistEvent(EventType eventType, String event) {
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("time_block", dtf.print(new DateTime()));
		obj.append("EventType", eventType.toString());
		obj.append(eventType.toString(), event);
		insert(coll, obj);
	}
	
	public void persistEvent(DBObject obj) {
		obj.put("time_block", dtf.print(new DateTime()));
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		insert(coll, obj);
	}

	public void registerFactoryAgent(String path, ServiceType serviceType) {
		DBCollection coll = db.getCollection(FACTORY_AGENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("time_block", dtf.print(new DateTime()));
		obj.append("FactoryAgentPath", path);
		obj.append("ServiceType", serviceType.toString());
		insert(coll, obj);
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
	
	public boolean finishedAllCompositions(String run) {
		System.out.println("Checking run:" + run );
		
		final DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		final BasicDBObject query = new BasicDBObject("run", run);
		query.append("EventType", "SERVICE_COMPOSITION_SUMMARY");
		
		final int clientAgents = db.getCollection("clientAgents").find().size();
		final int completedCompositions = coll.find(query).size();
		
		if (completedCompositions > 0 && clientAgents == completedCompositions) {
			return true;
		} else {
			return false;
		}
		
	}

	public static ModelStorage getInstance(DB db) {
		return new ModelStorage(db);
	}
	
}
