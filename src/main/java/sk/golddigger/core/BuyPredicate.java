package sk.golddigger.core;

import static sk.golddigger.core.RequestDateTime.DateUnit.MONTH;
import static sk.golddigger.core.RequestDateTime.DateUnit.WEEK;
import static sk.golddigger.core.RequestDateTime.DateUnit.YEAR;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sk.golddigger.core.RequestDateTime.DateUnit;

@Component
public class BuyPredicate extends MarketPredicate {

	private static final Logger logger = Logger.getLogger(BuyPredicate.class);

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
			System.exit(1);
		}
	}

	private void initDateUnitList() {
		final String predicateMessageCode = "predicate";
		registeredDateUnits = new ArrayList<>(3);
		if (week != 0) {
			registeredDateUnits.add(WEEK);
			logger.info(resolveMessage(predicateMessageCode, "week", week));
		}
		if (month != 0) {
			registeredDateUnits.add(MONTH);
			logger.info(resolveMessage(predicateMessageCode, "month", month));
		}
		if (year != 0) {
			registeredDateUnits.add(YEAR);
			logger.info(resolveMessage(predicateMessageCode, "year", year));
		}
	}

	@Override
	public boolean testMarket(Market market) {
		return this.predicate.test(market);
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
	 * performs or() on the underlying predicate
	 */
	@Override
	public void addPredicate(Predicate<Market> additionalPredicate) {
		this.predicate = this.predicate.or(additionalPredicate);

		if (!this.hasAdditionalPredicates) {
			this.hasAdditionalPredicates = true;
		}
	}

	@Override
	public Predicate<Market> getUnderlyingPredicate() {
		return this.predicate;
	}
}
