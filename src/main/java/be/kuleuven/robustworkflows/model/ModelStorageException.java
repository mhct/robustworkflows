package be.kuleuven.robustworkflows.model;

import com.mongodb.DBObject;

public class ModelStorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private DBObject obj;

	public ModelStorageException(DBObject obj) {
		this.obj = obj;
	}

	public DBObject getObj() {
		return obj;
	}
}
