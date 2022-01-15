package sk.golddigger.core;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sk.golddigger.core.RequestDateTime.DateUnit;
import sk.golddigger.exceptions.UnsupportedConfiguration;

@Component
public class BuyPredicate extends MarketPredicate {

	private static final Logger logger = Logger.getLogger(MarketPredicate.class);

	private Predicate<Market> predicate;
	private List<DateUnit> registeredDateUnits;

	public BuyPredicate(
			@Value("-${buy_predicate.week:0}") int week,
			@Value("-${buy_predicate.month:0}") int month,
			@Value("-${buy_predicate.year:0}") int year) {
		this.week = week;
		this.month = month;
		this.year = year;

		validateBuyPredicateSetting();
		initDateUnitList();

		this.predicate = constructPredicate();
	}

	private void validateBuyPredicateSetting() {
		if ((week + month + year) == 0) {
			logger.error(resolveMessage("unspecifiedPredicates"));
			throw new UnsupportedConfiguration("Unspecified predicates.");
		}
	}

	private void initDateUnitList() {
		registeredDateUnits = new ArrayList<>(3);
		if (week != 0) {
			registeredDateUnits.add(DateUnit.WEEK);
			logger.info(resolveMessage("predicate", "week", week));
		}
		if (month != 0) {
			registeredDateUnits.add(DateUnit.MONTH);
			logger.info(resolveMessage("predicate", "month", month));
		}
		if (year != 0) {
			registeredDateUnits.add(DateUnit.YEAR);
			logger.info(resolveMessage("predicate", "year", year));
		}
	}
	
	@Override
	public boolean testMarket(Market market) {
		return this.predicate.test(market);
	}

	private int getPercentageDifference(double current, double opening) {
		return (int) (((current - opening) / opening) * 100);
	}

	@Override
	public List<DateUnit> getPredicatedDateUnits() {
		return this.registeredDateUnits;
	}

	@Override
	public String toString() {
		return "week predicate: " + week + "%\n"
			+ "month predicate: " + month + "%\n"
			+ "year predicate: " + year + "%\n";
	}

	private Predicate<Market> constructPredicate() {
		Predicate<Market> weekPredicate = m -> false;
		Predicate<Market> monthPredicate = m -> false;
		Predicate<Market> yearPredicate = m -> false;

		// TODO: zalogovat vypocitane hodnoty
		if (week != 0) {
			weekPredicate = m -> getPercentageDifference(m.getCurrentPrice(), m.getFirstDayOfWeekOpeningPrice()) < week;
		}
		if (month != 0) {
			monthPredicate = m -> getPercentageDifference(m.getCurrentPrice(), m.getFirstDayOfMonthOpeningPrice()) < month;
		}
		if (year != 0) {
			yearPredicate = m -> getPercentageDifference(m.getCurrentPrice(), m.getFirstDayOfYearOpeningPrice()) < year;
		}

		return weekPredicate.or(monthPredicate).or(yearPredicate);
	}

	/**
	 * performs and() on the underlying predicate
	 */
	@Override
	public void addPredicate(Predicate<Market> additionalPredicate) {
		this.predicate.and(additionalPredicate);
	}

	@Override
	public Predicate<Market> getUnderlyingPredicate() {
		return this.predicate;
	}
}
