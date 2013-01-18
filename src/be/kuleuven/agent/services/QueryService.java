package be.kuleuven.agent.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import be.kuleuven.agent.AgentLoader;

@Path("query")
public class QueryService {

	@GET()
	@Path("/{quoteId}")
	@Produces("application/json")
	public BLA getQuery(@PathParam("quoteId") String quoteId) {
		addRequest(Long.valueOf(quoteId), System.currentTimeMillis());
		return new BLA();
	}
	
	private void addRequest(Long id, Long payload) {
		AgentLoader.getQueue().addRequest(id, payload);
	}
}

class BLA {
	public String name = "mario";
	public Integer age = 33;
}
