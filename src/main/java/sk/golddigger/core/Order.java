package sk.golddigger.core;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

public class Order {

	private static final String INSTANTIATION_MESSAGE_CODE = "factoryClassInstantiationError";

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

	public static class TimeInForce {

		private TimeInForce() {
			throw new IllegalStateException(resolveMessage(INSTANTIATION_MESSAGE_CODE, TimeInForce.class));
		}

		public static final String GTC = "GTC";
		public static final String GTT = "GTT";
		public static final String IOC = "IOC";
		public static final String FOK = "FOK";
		// add description
	}

	public static class CancelAfter {

		private CancelAfter() {
			throw new IllegalStateException(resolveMessage(INSTANTIATION_MESSAGE_CODE, CancelAfter.class));
		}

		public static final String MIN = "min";
		public static final String HOUR = "hour";
		public static final String DAY = "day";
	}

	public static class OrderType {

		private OrderType() {
			throw new IllegalStateException(resolveMessage(INSTANTIATION_MESSAGE_CODE, OrderType.class));
		}

		public static final String LIMIT = "limit";
		public static final String MARKET = "market";
	}

	public static class STP {

		private STP() {
			throw new IllegalStateException(resolveMessage(INSTANTIATION_MESSAGE_CODE, STP.class));
		}

		public static final String DECREASE_AND_CANCEL = "dc";
		public static final String CANCEL_OLDEST = "co";
		public static final String CANCEL_NEWEST = "cn";
		public static final String CANCEL_BOTH = "cb";
	}

	public static class Side {

		private Side() {
			throw new IllegalStateException(resolveMessage(INSTANTIATION_MESSAGE_CODE, Side.class));
		}

		public static final String BUY = "buy";
		public static final String SELL = "sell";
	}
}
