package be.kuleuven.robustworkflows.model;

import com.mongodb.DB;

/**
 * All the logging events related to the MODEL will be stored on a central DB
 * 
 * @author mario
 *
 */
public interface EventDB {
	public void setDB(DB db);
}
