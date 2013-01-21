package be.kuleuven.agent;

import java.util.logging.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * The CommInitiator is a processor of EventResponses. It is responsible for 
 * dispatching events to peers addressed in the EventResponse. It can be seen as a postman 
 * responsible for deliverying letters.
 * j
 * @author mariohct
 *
 */
public class CommInitiator implements Runnable {
	private static final Logger logger = Logger.getLogger(CommInitiator.class.getName());
	
	private Thread runner;

	private EventResponseQueueReader sourceQueue;
	private EventRequestQueuePublisher pubQueue;
	private Client wsclient;

	public CommInitiator(String name, Client wsclient, EventResponseQueueReader responseQueue, EventRequestQueuePublisher pubQueue) {
		logger.info("Created CommInitiator");
	
		this.wsclient = wsclient;
		this.sourceQueue = responseQueue;
		this.pubQueue = pubQueue;
		runner = new Thread(this, name);

	}

	public void run() {
		for(;;) {
			try {
				Thread.sleep(100);
				EventResponse er = sourceQueue.fetchEvent();
				if(er != null) {
					logger.info("received a request... should start contacting neighbors" + er);
					
				}
				
			} catch( InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void start() {
		logger.info("Starting CommInitiator: " + runner.getName());
		
		runner.start();
	}
	
	public static CommInitiator newInstance(String name, EventResponseQueueReader responseQueue, EventRequestQueuePublisher pubQueue) {
		if(name == null || responseQueue == null || pubQueue == null) {
			throw new IllegalArgumentException("name, EventResponseQueue, and pubQueue can not be null");
		}
		
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
		Client client = Client.create(clientConfig);
		
		CommInitiator initiator = new CommInitiator("Comm" + name, client, responseQueue, pubQueue);
		return initiator;
	}

}
