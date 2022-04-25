package sk.golddigger.core;

public class Order {

	private String profileId;
	private String productId;
	private String type;
	private String side;
	private Double price;
	private Double size;
	private String timeInForce;
	private Boolean postOnly;
	private String cancelAfter;
	private Double funds;
	private String stp;

	private Order() {
		
	}

	public String getProfileId() {
		return profileId;
	}

	public String getProductId() {
		return productId;
	}

	public String getType() {
		return type;
	}

	public String getSide() {
		return side;
	}

	public Double getPrice() {
		return price;
	}

	public Double getSize() {
		return size;
	}

	public String getTimeInForce() {
		return timeInForce;
	}

	public Boolean getPostOnly() {
		return postOnly;
	}

	public String getCancelAfter() {
		return cancelAfter;
	}

	public Double getFunds() {
		return funds;
	}

	public String getSelfTradePrevention() {
		return stp;
	}

	public static class OrderCreator {

		private final Order order = new Order();

		public OrderCreator setProfileId(String profileId) {
			order.profileId = profileId;
			return this;
		}

		public OrderCreator setProductId(String productId) {
			order.productId = productId;
			return this;
		}

		public OrderCreator setType(String type) {
			order.type = type;
			return this;
		}

		public OrderCreator setSide(String side) {
			order.side = side;
			return this;
		}

		public OrderCreator setPrice(Double price) {
			order.price = price;
			return this;
		}

		public OrderCreator setSize(Double size) {
			order.size = size;
			return this;
		}

		public OrderCreator setTimeInForce(String timeInForce) {
			order.timeInForce = timeInForce;
			return this;
		}

		public OrderCreator setPostOnly(Boolean postOnly) {
			order.postOnly = postOnly;
			return this;
		}

		public OrderCreator setCancelAfter(String cancelAfter) {
			order.cancelAfter = cancelAfter;
			return this;
		}

		public OrderCreator setFunds(Double funds) {
			order.funds = funds;
			return this;
		}

		public OrderCreator setSelfTradePrevention(String stp) {
			order.stp = stp;
			return this;
		}

		public Order createOrder() {
			return order;
		}
	}
}
