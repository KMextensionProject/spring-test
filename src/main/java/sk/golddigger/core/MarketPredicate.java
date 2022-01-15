package sk.golddigger.core;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;

import sk.golddigger.core.RequestDateTime.DateUnit;

public abstract class MarketPredicate {

	protected int week;
	protected int month;
	protected int year;

	@Autowired
	protected Market market;

	public int getWeekPredicatePercentage() {
		return this.week;
	}

	public int getMonthPredicatePercentage() {
		return this.month;
	}

	public int getYearPredicatePercentage() {
		return this.year;
	}

	public abstract void addPredicate(Predicate<Market> predicate);

	public abstract Predicate<Market> getUnderlyingPredicate();

	public abstract boolean testMarket();

	public abstract List<DateUnit> getPredicatedDateUnits();

}
