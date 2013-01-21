package be.kuleuven.agent;

import java.util.logging.Logger;

public class Agent implements Runnable {

	private static final Logger logger = Logger.getLogger(Agent.class.getName());
	private Thread runner;
	private EventRequestQueueReader requestsQueue;
	private EventResponseQueuePublisher responseQueue;
	
	public void run() {
		System.out.println("Running");

		for(;;) {
			try {
				Thread.sleep(3000);
				
				EventRequest er = requestsQueue.fetchRequest();
				if(er != null) {
					logger.info("fetched request: " + er.toString());
					responseQueue.add(EventResponse.from(er));
				}
		
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Agent(String name, EventRequestQueueReader requestsQueue, EventResponseQueuePublisher responseQueue) {
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
