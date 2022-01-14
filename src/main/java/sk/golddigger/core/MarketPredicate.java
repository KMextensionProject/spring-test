package sk.golddigger.core;

import java.util.List;
import java.util.function.Predicate;

import sk.golddigger.core.RequestDateTime.DateUnit;

public abstract class MarketPredicate {

	protected int week;
	protected int month;
	protected int year;

	public int getWeekPredicatePercentage() {
		return this.week;
	}

	public int getMonthPredicatePercentage() {
		return this.month;
	}

	public int getYearPredicatePercentage() {
		return this.year;
	}

	public abstract Predicate<Market> constructPredicate();

	public abstract List<DateUnit> getPredicatedDateUnits();

}
