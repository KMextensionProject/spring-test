package sk.golddigger.core;

public class Order {

	private String profile_id;
	private String product_id;
	private String type;
	private String side;
	private Double price;
	private Double size;
	private String time_in_force;
	private Boolean post_only;
	private String cancel_after;
	private Double funds;
	private String stp;

	private Order(OrderCreator orderCreator) {
		this.profile_id = (orderCreator.profile_id == null) ? "default profile_id" : orderCreator.profile_id;
		this.product_id = orderCreator.product_id;
		this.type = orderCreator.type;
		this.side = orderCreator.side;
		this.price = orderCreator.price;
		this.size = orderCreator.size;
		this.time_in_force = orderCreator.time_in_force;
		this.post_only = orderCreator.post_only;
		this.cancel_after = orderCreator.cancel_after;
		this.funds = orderCreator.funds;
		this.stp = orderCreator.stp;
	}

	public String getProfileId() {
		return profile_id;
	}

	public String getProductId() {
		return product_id;
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
		return time_in_force;
	}

	public Boolean getPostOnly() {
		return post_only;
	}

	public String getCancelAfter() {
		return cancel_after;
	}

	public Double getFunds() {
		return funds;
	}

	public String getStp() {
		return stp;
	}

	public static class OrderCreator {

		private String profile_id;
		private String product_id;
		private String type;
		private String side;
		private Double price;
		private Double size;
		private String time_in_force;
		private Boolean post_only;
		private String cancel_after;
		private Double funds;
		private String stp;

		public OrderCreator setProfile_id(String profile_id) {
			this.profile_id = profile_id;
			return this;
		}

		public OrderCreator setProduct_id(String product_id) {
			this.product_id = product_id;
			return this;
		}

		public OrderCreator setType(String type) {
			this.type = type;
			return this;
		}

		public OrderCreator setSide(String side) {
			this.side = side;
			return this;
		}

		public OrderCreator setPrice(Double price) {
			this.price = price;
			return this;
		}

		public OrderCreator setSize(Double size) {
			this.size = size;
			return this;
		}

		public OrderCreator setTime_in_force(String time_in_force) {
			this.time_in_force = time_in_force;
			return this;
		}

		public OrderCreator setPost_only(Boolean post_only) {
			this.post_only = post_only;
			return this;
		}

		public OrderCreator setCancel_after(String cancel_after) {
			this.cancel_after = cancel_after;
			return this;
		}

		public OrderCreator setFunds(Double funds) {
			this.funds = funds;
			return this;
		}

		public OrderCreator setSelfTradePrevention(String stp) {
			this.stp = stp;
			return this;
		}

		public Order createOrder() {
			return new Order(this);
		}
	}

	public class TimeInForce {
		public static final String GTC = "GTC";
		public static final String GTT = "GTT";
		public static final String IOC = "IOC";
		public static final String FOK = "FOK";
		// add description
	}

	public class CancelAfter {
		public static final String MIN = "min";
		public static final String HOUR = "hour";
		public static final String DAY = "day";
	}

	public class OrderType {
		public static final String LIMIT = "limit";
		public static final String MARKET = "market";
	}

	public class STP {
		public static final String DECREASE_AND_CANCEL = "dc";
		public static final String CANCEL_OLDEST = "co";
		public static final String CANCEL_NEWEST = "cn";
		public static final String CANCEL_BOTH = "cb";
	}

	public class Side {
		public static final String BUY = "buy";
		public static final String SELL = "sell";
	}
}
