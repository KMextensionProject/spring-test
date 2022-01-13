package sk.golddigger.enums;

public enum Currency {

	EURO("EUR"),
	DOLLAR("USD"),

	BITCOIN("BTC"),
	POLKADOT("DOT"),
	ETHEREUM("ETH"),
	LITECOIN("LTC"),
	CARDANO("ADA"),
	EOS("EOS"),
	XRP("XRP") {
		@Override
		public String getName() {
			return "ripple";
		}
	},
	DOGECOIN("DOGE");

	private final String acronym;

	private Currency(String acronym) {
		this.acronym = acronym;
	}

	public String getAcronym() {
		return this.acronym;
	}

	/**
	 * @return the lowercase enum name
	 */
	public String getName() {
		return this.toString().toLowerCase();
	}
}
