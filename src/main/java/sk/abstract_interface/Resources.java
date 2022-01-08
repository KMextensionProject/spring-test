package sk.abstract_interface;

/**
 * zoznam url alebo lokacii sablon, ci inych zdrojov
 *
 */
public class Resources {

	public static final String CURRENT_BITCOIN_PRICE_URL = "https://api.coinbase.com/v2/prices/spot?currency=$";
	public static final String BITCOIN_PRICE_BY_DATE_URL = "https://api.polygon.io/v2/aggs/ticker/X:BTC$/range/1/day/$/$?adjusted=true&sort=asc&limit=120&apiKey=$";

	/**
	 * comment method and other description
	 */
	public static final String COINBASE_ACCOUNT_BY_ID_URL = "https://api.exchange.coinbase.com/accounts/$";

	/**
	 * 
	 */
	public static final String COINBASE_ACCOUNTS_URL = "https://api.exchange.coinbase.com/accounts";
	public static final String COINBASE_ORDER_FILLS = "https://api.exchange.coinbase.com/fills?product_id=BTC-$&profile_id=default&limit=100";

	public static final String APPLICATION_JSON = "application/json";

	public static final String MESSAGES_MAP = "messages.properties";

	private Resources() { }

	
}
