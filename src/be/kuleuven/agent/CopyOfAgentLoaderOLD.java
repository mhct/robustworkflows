package be.kuleuven.agent;

import java.lang.reflect.Method;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

public class CopyOfAgentLoaderOLD {
//	private static final Configuration config = new EnvironmentConfiguration(); 
	private static final String DEFAULT_LOCAL_ADDRESS = "http://localhost";
	private static final Integer DEFAULT_LOCAL_PORT = 8089;
	static Float DEFAULT_BLA = 1.0f;
	
//	private static final String LOCAL_ADDRESS = (System.getenv("LOCAL_ADDRESS") == null) ? DEFAULT_LOCAL_ADDRESS : System.getenv("LOCAL_ADDRESS");
//	private static final Integer LOCAL_PORT = (System.getenv("LOCAL_PORT") == null) ? DEFAULT_LOCAL_PORT : Integer.valueOf(System.getenv("LOCAL_PORT"));
	
	
	
//	private static final String LOCAL_ADDRESS = getEnvironmentValue("LOCAL_ADDRESS");
//	private static final Integer LOCAL_PORT = getEnvironmentValue("LOCAL_PORT");
	private static Float BLA = getEnvironmentValue("BLA");
	
//	private static final URI BASE_URI = getBaseURI(LOCAL_ADDRESS, LOCAL_PORT);

	static {
		System.out.println("static block analysis");
		System.out.println((Integer)getEnvironmentValue("BLA"));
		for(Method a: CopyOfAgentLoaderOLD.class.getMethods()) {
			System.out.println("m: " + a.toGenericString());
		}
		
		
		
	}
	
//	@SuppressWarnings("unchecked")
	public static <T> T getEnvironmentValue(String envVariable) {
		
		try {
			if( System.getenv(envVariable) == null ) {
				return (T)CopyOfAgentLoaderOLD.class.getDeclaredField("DEFAULT_" + envVariable).get(null);
			} else {
				return (T)System.getenv(envVariable);
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		throw new RuntimeException("Not existing value or Environment Variable");
		
	}
	
	/**
	 * Defines the interface and port where the Agent is going to listen to connections.
	 * @param localAddress
	 * @param localPort
	 * @return
	 */
	public static URI getBaseURI(String localAddress, Integer localPort) {
		
		return UriBuilder.fromUri(localAddress).port(localPort).build();
	}
	
	/**
	 * Loads a complete agent including the communicaiton infrastructure
	 * @param args
	 * Environment Variables
	 * IP
	 * PORT
	 */
	public static void main(String[] args) {
//		System.out.println(LOCAL_ADDRESS);
//		System.out.println(LOCAL_PORT);
		System.out.println(BLA);
	}

}
