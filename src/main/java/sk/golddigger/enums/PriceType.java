package sk.golddigger.enums;

public enum PriceType {

	OPENING("o"),
	CLOSING("c"),
	HIGHEST("h"),
	LOWEST("l");

	private final String mark;

	private PriceType(String mark) {
		this.mark = mark;
	}

	public String getMark() {
		return this.mark;
	}
}
