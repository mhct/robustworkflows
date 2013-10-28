package be.kuleuven.robustworkflows.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.BasicBSONObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import be.kuleuven.robustworkflows.model.clientagent.EventType;
import be.kuleuven.robustworkflows.model.events.ModelEvent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;


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
	private Multimap<DBCollection, DBObject> writeCache;
	
	
	private ModelStorage(DB db) {
		this.db = db;
		extraFields = Maps.newHashMap();
		writeCache = ArrayListMultimap.create();
	}

	public void addField(String key, Object value) {
		extraFields.put(key, value);
	}
	
	public Object getField(String key) {
		return extraFields.get(key);
	}
	
	/**
	 * This method adds extra fields, needed for running multiple experiments, to the persisted object.
	 * 
	 * @param coll
	 * @param obj
	 */
	private void insert(DBCollection coll, DBObject obj) {
		for (Map.Entry<String, Object> e: extraFields.entrySet()) {
			obj.put(e.getKey(), e.getValue());
		}
		writeToDB(coll, obj);
	}
	
	private void writeToDB(DBCollection coll, DBObject obj) {
		
		writeCache.put(coll, obj);
		if (writeCache.size() > 30) {
			persistWriteCache();
		}
		
	}
	
	public void persistWriteCache() {
		for (int i=0; i<writeCache.keySet().size(); i++ ) {
			for (DBCollection currentColl : writeCache.keySet()) {
				List<DBObject> objects = new ArrayList<DBObject>(writeCache.get(currentColl));
				writeCache.removeAll(currentColl);
				
				try {
					currentColl.insert(objects);
				} catch (MongoException e) {
					throw new RuntimeException("Mongo Exc." + e);
				} catch (Exception e) {
					throw new RuntimeException("I/O Exc." + e);
				}
			}
		}
	}
	
	/**
	 * Persists an event in the database
	 * 
	 * @param event
	 */
	public void persistEvent(ModelEvent event) {
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("time_block", dtf.print(new DateTime()));
		obj.append(ModelStorageMap.EVENT_TYPE, event.eventType());
		if (event.values() != null) {
			obj.putAll(event.values());
		}
		insert(coll, obj);
	}
	
	public void persistEvent(EventType eventType, String event) {
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("time_block", dtf.print(new DateTime()));
		obj.append(ModelStorageMap.EVENT_TYPE, eventType.toString());
		obj.append(eventType.toString(), event);
		insert(coll, obj);
	}
	
	public void persistEvent(DBObject obj) {
		obj.put("time_block", dtf.print(new DateTime()));
		DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		insert(coll, obj);
	}

	/**
	 * TODO refactor this method to a service class
	 * 
	 * @param path
	 * @param serviceType
	 */
	public void registerFactoryAgent(String path, ServiceType serviceType) {
		DBCollection coll = db.getCollection(FACTORY_AGENTS_COLLECTION);
		BasicDBObject obj = new BasicDBObject("time_block", dtf.print(new DateTime()));
		obj.append("FactoryAgentPath", path);
		obj.append("ServiceType", serviceType.toString());
		insert(coll, obj);
	}

	/**
	 * TODO refactor this method to a service class
	 * 
	 * @param st
	 * @return
	 */
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

	/**
	 * TODO refactor this method to a service class
	 * 
	 * @param path
	 */
	public void unRegisterFactoryAgent(String path) {
		DBCollection coll = db.getCollection(FACTORY_AGENTS_COLLECTION);
		BasicDBObject query = new BasicDBObject("FactoryAgentPath", path);
		
		coll.remove(query);
	}
	
	/**
	 * TODO refactor this method to a service class
	 * 
	 * @param run
	 * @return
	 */
	public boolean finishedAllCompositions(String run) {
		System.out.println("Checking run:" + run );
		
		final DBCollection coll = db.getCollection(EVENTS_COLLECTION);
		final BasicDBObject query = new BasicDBObject("run", run);
		query.append(ModelStorageMap.EVENT_TYPE, ModelStorageMap.SERVICE_COMPOSITION_SUMMARY);
		
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
