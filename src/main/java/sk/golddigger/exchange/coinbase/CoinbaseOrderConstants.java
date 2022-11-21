package sk.golddigger.coinbase;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

public class CoinbaseOrderConstants {

	private static final String INSTANTIATION_MESSAGE_CODE = "factoryClassInstantiationError";

	private CoinbaseOrderConstants() {
		throw new IllegalStateException(resolveMessage(INSTANTIATION_MESSAGE_CODE, CoinbaseOrderConstants.class));
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
