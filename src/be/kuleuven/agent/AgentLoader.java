package be.kuleuven.agent;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.EnvironmentConfiguration;
import org.glassfish.grizzly.http.server.HttpServer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;


public class AgentLoader implements EventRequestQueue, EventRequestQueuePublisher {
	
	private static final Logger logger = Logger.getLogger(AgentLoader.class.getName());
	
	private static final Configuration config = new EnvironmentConfiguration(); 
	private static final String LOCAL_ADDRESS = config.getString("LOCAL_ADDRESS", "http://localhost");
	private static final Integer LOCAL_PORT = config.getInteger("LOCAL_PORT", 8089);
	private static final String AGENT_NAME = config.getString("AGENT_NAME", "droid-0");
	
	private static URI BASE_URI = getBaseURI(LOCAL_ADDRESS, LOCAL_PORT);

	private static AgentLoader agentLoaderInstance;
	
	private static final List<EventRequest> requestsQueue = Lists.newArrayList();
	
	private Agent agent;
	private HttpServer server;
	
	private AgentLoader() throws IOException {
		server = startServer();
		agent = Agent.newInstance(AGENT_NAME, (EventRequestQueue) this);
		
	}
	
	public void start() {
		agent.start();
		
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.stop();
		agent.stop();
	}
	
	/**
	 * Defines the interface and port where the Agent is going to listen to connections.
	 * @param localAddress
	 * @param localPort
	 * @return
	 */
	private static URI getBaseURI(String localAddress, Integer localPort) {
		return UriBuilder.fromUri(localAddress).port(localPort).build();
	}
	
	private static Map<String, Object> getHttpServerConfiguration() {
		Map<String, Object> configurations = Maps.newHashMap();
		
		configurations.put("com.sun.jersey.api.json.POJOMappingFeature", Boolean.TRUE);
		
		return Collections.unmodifiableMap(configurations);
	}
	
	private static HttpServer startServer() throws IOException {
		logger.info("Starting HTTP server at: " + LOCAL_ADDRESS + " port: " + LOCAL_PORT);
		
		ResourceConfig rc = new PackagesResourceConfig("be.kuleuven.agent.services");
		rc.setPropertiesAndFeatures(getHttpServerConfiguration());
		return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
	}
	
	/**
	 * Loads a complete agent including the communicaiton infrastructure
	 * @param args
	 * Environment Variables
	 * IP
	 * PORT
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		agentLoaderInstance = new AgentLoader();
		agentLoaderInstance.start();
	}

	public synchronized void addRequest(Long id, Long payload) {
		requestsQueue.add(EventRequest.newInstance(id, payload));
	}

	public synchronized EventRequest fetchRequest() {
		if( requestsQueue.isEmpty() ) {
			return null;
		} else {
			return requestsQueue.remove(0);
		}
	}

	public static EventRequestQueuePublisher getQueue() {
		return (EventRequestQueuePublisher) agentLoaderInstance;
	}
	
	public static void add(Long id, Long payload) {
		agentLoaderInstance.addRequest(id, payload);
	}

}
