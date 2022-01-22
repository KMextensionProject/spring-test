package sk.golddigger.core;

import java.time.LocalDate;

public abstract class Market implements Updatable {

	protected double currentPrice;
	protected double firstDayOfWeekOpeningPrice;
	protected double firstDayOfWeekClosingPrice;
	protected double firstDayOfMonthOpeningPrice;
	protected double firstDayOfMonthClosingPrice;
	protected double firstDayOfYearOpeningPrice;
	protected double allTimeHigh;
	protected LocalDate lastUpdated;

	public double getCurrentPrice() {
		return currentPrice;
	}

	public double getFirstDayOfWeekOpeningPrice() {
		return firstDayOfWeekOpeningPrice;
	}

	public double getFirstDayOfWeekClosingPrice() {
		return firstDayOfWeekClosingPrice;
	}

	public double getFirstDayOfMonthOpeningPrice() {
		return firstDayOfMonthOpeningPrice;
	}

	public double getFirstDayOfMonthClosingPrice() {
		return firstDayOfMonthClosingPrice;
	}

	public double getFirstDayOfYearOpeningPrice() {
		return firstDayOfYearOpeningPrice;
	}

	public double getAllTimeHigh() {
		return allTimeHigh;
	}

	public LocalDate getLastUpdated() {
		return lastUpdated;
	}

}
