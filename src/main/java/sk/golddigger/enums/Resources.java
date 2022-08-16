package sk.golddigger.enums;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

/**
 * Contains constants of URL endpoints, file locations and other constants.
 */
public class Resources {

	public static final String CURRENT_CRYPTO_PRICE_URL = "https://api.coingecko.com/api/v3/coins/$?localization=false&tickers=false&market_data=true&community_data=false&developer_data=false&sparkline=false";
	public static final String CRYPTO_PRICE_BY_DATE_URL = "https://api.polygon.io/v2/aggs/ticker/X:$$/range/1/day/$/$?adjusted=true&sort=asc&limit=120&apiKey=$";

	public static final String COINBASE_ACCOUNT_BY_ID_URL = "/accounts/$";
	public static final String COINBASE_ACCOUNTS_URL = "/accounts";
	public static final String COINBASE_ORDER_FILLS_URL = "/fills?product_id=$-$&profile_id=default&limit=100";
	public static final String COINBASE_PLACE_ORDER_URL = "/orders";
	public static final String COINBASE_ORDER_BY_ID_URL = "/orders/$";

	public static final String TELEGRAM_SEND_MASSAGE_URL = "https://api.telegram.org/bot$/sendMessage?chat_id=$&text=$&parse_mode=html";

	public static final String JSP_VIEW_RESOURCES = "/WEB-INF/views/";
	public static final String MESSAGES_MAP = "messages.properties";

	public static final String FILLED_ORDERS_TEMPLATE = "/reports/Filled_orders.xlsx";

	private Resources() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", Resources.class));
	}

}
