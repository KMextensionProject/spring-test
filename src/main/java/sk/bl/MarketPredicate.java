package sk.bl;

import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sk.abstract_interface.Market;

@Component
public class MarketPredicate {

	@Value("-${buy_predicate.week:0}")
	private int week;

	@Value("-${buy_predicate.month:0}")
	private int month;

	@Value("-${buy_predicate.year:0}")
	private int year;

	public void validateBuyPredicateSetting() {
		if ((week + month + year) == 0) {
			throw new IllegalStateException("no predicates specified");
		}
	}

	public Predicate<Market> constructBuyPredicate() {
		Predicate<Market> weekPredicate = m -> false;
		Predicate<Market> monthPredicate = m -> false;
		Predicate<Market> yearPredicate = m -> false;

		if (week != 0) {
			weekPredicate = m -> getPercentageDifference(m.getCurrentPrice(), m.getWeekOpeningPrice()) < week;
		}
		if (month != 0) {
			monthPredicate = m -> getPercentageDifference(m.getCurrentPrice(), m.getMonthOpeningPrice()) < month;
		}
		if (year != 0) {
			yearPredicate = m -> getPercentageDifference(m.getCurrentPrice(), m.getYearOpeningPrice()) < year;
		}

		return weekPredicate.or(monthPredicate).or(yearPredicate);
	}

	private int getPercentageDifference(double current, double opening) {
		return (int) (((current - opening) / opening) * 100);
	}
}
