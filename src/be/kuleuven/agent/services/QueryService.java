package be.kuleuven.agent.services;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlRootElement;

import be.kuleuven.agent.AgentLoader;
import be.kuleuven.agent.EventPayload;
import be.kuleuven.agent.ProductionQuote;

@Path("quote")
public class QueryService {
	private static final Logger logger = Logger.getLogger(QueryService.class.getName());
	
	@GET
	@Path("/{amount}")
	@Produces("application/json")
	public BLA getQuery(@PathParam("amount") String originUri) {
//		String originUri = "";
		logger.finest("getQuery called: " + originUri);
		addRequest(originUri, ProductionQuote.newInstance(originUri, 0, 0));
		return new BLA("mama", 33);
	}
	
	@POST
	@Path("/ma")
	@Consumes("application/json")
	public void postQuotation(BLA thing) {
		logger.info("postQuotation called. Received quotation, quoteId: " + thing.name);
	}
	
	private void addRequest(String originUri, EventPayload payload) {
		AgentLoader.add(originUri, payload);
	}
}

@XmlRootElement
class BLA {
	public String name;
	public Integer age;
	
	public BLA() {}
	
	public BLA(String name, Integer age) {
		this.name = name;
		this.age = age;
	}
}
