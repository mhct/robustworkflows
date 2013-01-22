package be.kuleuven.agent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Agent implements Runnable {

	private static final Logger logger = Logger.getLogger(Agent.class.getName());
	private Thread runner;
	private EventRequestQueueReader requestsQueue;
	private EventResponseQueuePublisher responseQueue;
	
	public final void run() {
		System.out.println("Running");

		for(;;) {
			try {
				Thread.sleep(3000);
				
				EventRequest er = requestsQueue.fetchRequest();
				if(er != null) {
					logger.info("fetched request: " + er.toString());
					handleRequest(er);
				}
		
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	void handleRequest(EventRequest er) {
		throw new RuntimeException("This method has to be instatiated by a subclass");
	}
	
	final void addResponse(EventResponse er) {
		logger.finest("Adding EventResponse to EventReponseQueue. EventResponse: " + er);
		responseQueue.add(er);
	}

	Agent(String name, EventRequestQueueReader requestsQueue, EventResponseQueuePublisher responseQueue) {
		if(name == null || requestsQueue == null || responseQueue == null) {
			throw new IllegalArgumentException("name, requestQueue, responseQueue can not be null");
		}
		
		this.requestsQueue = requestsQueue;
		this.responseQueue = responseQueue;
		runner = new Thread(this, name);
	}
	

	public void start() {
		logger.info("I am alive" + System.currentTimeMillis());
		runner.start();
	}

	public void stop() {
		runner.interrupt();
	}

	public static Agent newInstance(String name, EventRequestQueueReader requestsQueue, EventResponseQueuePublisher responseQueue) {
		Agent agent = new Agent(name, requestsQueue, responseQueue);
		return agent;
	}
}
