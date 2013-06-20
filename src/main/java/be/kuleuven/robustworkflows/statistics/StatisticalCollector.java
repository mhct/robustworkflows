package be.kuleuven.robustworkflows.statistics;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Collects and analyses statistics from RobustWorflows experiments
 * 
 * @author mario
 *
 */
public class StatisticalCollector {
	
	private static final Config config = ConfigFactory.load().getConfig("robust-workflows");
	
	private static final String SYSTEM_NAME = config.getString("system-name");
	private static final String DB_SERVER_IP = config.getString("db-server-ip");
	private static final Integer DB_SERVER_PORT = config.getInt("db-server-port");
	private static final String DB_NAME = config.getString("db-name");

	private final DB db;
	
	public StatisticalCollector(DB db) {
		this.db = db;
	}

	public static void main(String[] args) throws UnknownHostException {
		final MongoClient client = new MongoClient(DB_SERVER_IP, DB_SERVER_PORT);
		final DB db = client.getDB(DB_NAME);
		
		StatisticalCollector collector = new StatisticalCollector(db);
//		collector.collect();
//		collector.toCSV();
	}
}
