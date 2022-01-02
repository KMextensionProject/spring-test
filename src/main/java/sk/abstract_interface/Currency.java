package sk.abstract_interface;

public enum Currency {

	EURO("EUR"),
	DOLLAR("USD"),

	BITCOIN("BTC"),
	ETHEREUM("ETH"),
	POLKADOT("DOT");

	private final String acronym;

	private Currency(String acronym) {
		this.acronym = acronym;
	}

	public String getAcronym() {
		return this.acronym;
	}
}
