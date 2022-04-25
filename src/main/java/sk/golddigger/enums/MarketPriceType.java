package sk.golddigger.enums;

public enum MarketPriceType {

	OPENING("o"),
	CLOSING("c"),
	HIGHEST("h"),
	LOWEST("l");

	private final String mark;

	private MarketPriceType(String mark) {
		this.mark = mark;
	}

	public String getMark() {
		return this.mark;
	}
}
