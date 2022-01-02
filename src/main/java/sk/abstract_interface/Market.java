package sk.abstract_interface;

public abstract class Market implements Refreshable {

	protected double currentPrice;

	protected double weekOpeningPrice;
	protected double weekClosingPrice;
	protected double monthOpeningPrice;
	protected double monthClosingPrice;
	protected double yearOpeningPrice;
	protected double allTimeHigh;

	public double getCurrentPrice() {
		return currentPrice;
	}

	public double getWeekOpeningPrice() {
		return weekOpeningPrice;
	}

	public double getWeekClosingPrice() {
		return weekClosingPrice;
	}

	public double getMonthOpeningPrice() {
		return monthOpeningPrice;
	}

	public double getMonthClosingPrice() {
		return monthClosingPrice;
	}

	public double getYearOpeningPrice() {
		return yearOpeningPrice;
	}

	public double getAllTimeHigh() {
		return allTimeHigh;
	}

}
