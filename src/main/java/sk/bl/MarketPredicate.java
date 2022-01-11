package sk.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import sk.abstract_interface.Market;
import sk.implementation.RequestTime.DateUnit;

@Component
public class MarketPredicate {

	@Value("-${buy_predicate.week:0}")
	private int week;

	@Value("-${buy_predicate.month:0}")
	private int month;

	@Value("-${buy_predicate.year:0}")
	private int year;

	private List<DateUnit> registeredDateUnits;

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

	private int getPercentageDifference(double current, double opening) {
		return (int) (((current - opening) / opening) * 100);
	}

	public List<DateUnit> getRegisteredDateUnits() {
		return this.registeredDateUnits;
	}

	public int getWeekPredicatePercentage() {
		return this.week;
	}

	public int getMonthPredicatePercentage() {
		return this.month;
	}

	public int getYearPredicatePercentage() {
		return this.year;
	}

	@Override
	public String toString() {
		return "week predicate: " + week + "%\n"
			+ "month predicate: " + month + "%\n"
			+ "year predicate: " + year + "%\n";
	}

	@PostConstruct
	private void initDateUnitList() {
		registeredDateUnits = new ArrayList<>(3);
		if (week != 0)
			registeredDateUnits.add(DateUnit.WEEK);
		if (month != 0)
			registeredDateUnits.add(DateUnit.MONTH);
		if (year != 0)
			registeredDateUnits.add(DateUnit.YEAR);
	}
}
