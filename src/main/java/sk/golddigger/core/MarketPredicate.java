package sk.golddigger.core;

import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;

import sk.golddigger.core.RequestDateTime.DateUnit;

/**
 * This class represents a predicate that in its principle compares the 
 * week, month and year percentage for market decrease specified by the
 * initial configuration.</br>
 * It also allows to add additional market conditions to the underlying 
 * predicate.
 */
public abstract class MarketPredicate {

	private static final Logger logger = Logger.getLogger(MarketPredicate.class);

	protected int week;
	protected int month;
	protected int year;
	protected boolean hasAdditionalPredicates;

	public int getWeekPredicatePercentage() {
		return this.week;
	}

	public int getMonthPredicatePercentage() {
		return this.month;
	}

	public int getYearPredicatePercentage() {
		return this.year;
	}

	public boolean containsAdditionalPredicates() {
		return this.hasAdditionalPredicates;
	}

	protected int getPercentageDifference(double current, double other) {
		int difference = (int) (((current - other) / other) * 100);
		if (logger.isDebugEnabled()) {
			logger.debug("Predicate rate difference: " + difference + "%");
		}
		return difference;
	}

	public abstract void addPredicate(Predicate<Market> predicate);

	public abstract Predicate<Market> getUnderlyingPredicate();

	public abstract boolean testMarket(Market market);

	public abstract List<DateUnit> getPredicatedDateUnits();

}
