package be.kuleuven.agent;

public class AssemblyAgent extends Agent {

	private Integer inputParts;
	private final Integer maximumProductionRate;
	private final Integer pricePerUnit;

	AssemblyAgent(String name, EventRequestQueueReader requestsQueue, EventResponseQueuePublisher responseQueue, Integer inputParts, Integer maximumProductionRate, Integer pricePerUnit) {
		super("aa", requestsQueue, responseQueue);
		
		this.inputParts = inputParts;
		this.maximumProductionRate = maximumProductionRate;
		this.pricePerUnit = pricePerUnit;
	}
	
	void handleRequest(EventRequest er) {
		//TODO fix this perhaps add a chain of responsibility
		if(ProductionQuote.class.isInstance(er.getPayload())) {
			ProductionQuote pq = (ProductionQuote) er.getPayload();
			addResponse(EventResponse.to(er.getOriginUri(), ProductionQuoteReply.newInstance(pq, pricePerUnit * pq.getAmount())));
		}
	}
	
	public static AssemblyAgent newInstance(String name, 
											EventRequestQueueReader requestsQueue, 
											EventResponseQueuePublisher responseQueue,
											Integer inputParts,
											Integer maximumProductionRate,
											Integer pricePerUnit) {
		
		AssemblyAgent aa = new AssemblyAgent(name, requestsQueue, responseQueue, inputParts, maximumProductionRate, pricePerUnit);
		return aa;
	}
}
