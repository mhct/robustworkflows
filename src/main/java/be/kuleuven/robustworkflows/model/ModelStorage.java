package be.kuleuven.robustworkflows.model;

import java.util.Date;

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

	public void persistEvent(String event) {
//		System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPOOORAOROAROAR\n\n\n");
		DBCollection coll = db.getCollection("robustworkflows");
		BasicDBObject obj = new BasicDBObject("current-time", new Date());
		obj.append("Event", event);
		coll.insert(obj);
	}
}
