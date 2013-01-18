package be.kuleuven.agent;

import java.util.logging.Logger;

public class Agent implements Runnable {

	private static final Logger logger = Logger.getLogger(Agent.class.getName());
	private Thread runner;
	private EventRequestQueue requestsQueue;
	
	public void run() {
		System.out.println("Running");

		for(;;) {
			try {
				Thread.sleep(3000);
				logger.info("fetched request: " + requestsQueue.fetchRequest());
		
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Agent(String name, EventRequestQueue requestsQueue) {
		this.requestsQueue = requestsQueue;
		runner = new Thread(this, name);
	}
	

	public void start() {
		logger.info("I am alive" + System.currentTimeMillis());
		runner.start();
	}

	public void stop() {
		runner.interrupt();
	}

	public static Agent newInstance(String name, EventRequestQueue requestsQueue) {
		Agent agent = new Agent(name, requestsQueue);
		return agent;
	}
}
