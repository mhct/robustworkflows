package be.kuleuven.agent;

public class ProductionQuoteReply implements EventPayload {

	private final ProductionQuote originalQuote;
	private final Integer price;
	
	private ProductionQuoteReply(ProductionQuote pq, Integer price) {
		this.originalQuote = pq;
		this.price = price;
	}

	public static ProductionQuoteReply newInstance(ProductionQuote pq, Integer price) {
		return new ProductionQuoteReply(pq, price);
	}

	@Override
	public String toString() {
		return "ProductionQuoteReply [originalQuote=" + originalQuote
				+ ", price=" + price + "]";
	}

	
}
