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
		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPOOORAOROAROAR\n\n\n");
		DBCollection coll = db.getCollection("robustworkflows");
		BasicDBObject obj = new BasicDBObject("current-time", System.currentTimeMillis());
		obj.append("Event", event);
		coll.insert(new BasicDBObject("Event", event));
	}
}
