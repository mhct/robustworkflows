package be.kuleuven.agent;


public class ProductionQuote implements EventPayload {
	private final String requesterUri;
	private final Integer producerId;
	private final Integer amount;

	private ProductionQuote(String requesterUri, Integer producerId, Integer amount) {
		this.requesterUri = requesterUri;
		this.producerId = producerId;
		this.amount = amount;
	}

	public String getRequesterUri() {
		return requesterUri;
	}


	public Integer getProducerId() {
		return producerId;
	}


	public Integer getAmount() {
		return amount;
	}


	public static ProductionQuote newInstance(String requesterUri, Integer producerId, Integer amount) {
		return new ProductionQuote(requesterUri, producerId, amount);
	}
}
