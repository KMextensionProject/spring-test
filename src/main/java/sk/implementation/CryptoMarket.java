package sk.implementation;

import static sk.abstract_interface.PriceType.CLOSING;
import static sk.abstract_interface.PriceType.OPENING;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.abstract_interface.Market;
import sk.abstract_interface.PriceType;
import sk.bl.MarketPredicate;
import sk.implementation.RequestTime.DateUnit;

@Component
public class CryptoMarket extends Market {

	@Autowired
	private CryptoMarketRequest cryptoMarketRequest;

	@Autowired
	private MarketPredicate marketPredicate;

	@Autowired
	private RequestTime requestTime;

	@Override
	public void updateState() throws Exception {
		this.currentPrice = cryptoMarketRequest.getCurrentPrice();
		updateOpeningAndClosingPrices();
	}

	// TODO: find a better way to express this
	private void updateOpeningAndClosingPrices() throws IOException {
		List<DateUnit> registeredDateUnits = marketPredicate.getRegisteredDateUnits();

		if (registeredDateUnits.contains(DateUnit.WEEK)) {
			Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(getFirstDayAdjusted(DateUnit.WEEK));
			this.firstDayOfWeekOpeningPrice = weekPrice.get(OPENING);
			this.firstDayOfWeekClosingPrice = weekPrice.get(CLOSING);
		}

		if (registeredDateUnits.contains(DateUnit.MONTH)) {
			Map<PriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(getFirstDayAdjusted(DateUnit.MONTH));
			this.firstDayOfMonthOpeningPrice = monthPrice.get(OPENING);
			this.firstDayOfMonthClosingPrice = monthPrice.get(CLOSING);
		}

		if (registeredDateUnits.contains(DateUnit.YEAR)) {
			Map<PriceType, Double> yearPrice = cryptoMarketRequest.getPricesByDate(getFirstDayAdjusted(DateUnit.YEAR));
			this.firstDayOfYearOpeningPrice = yearPrice.get(OPENING);
			this.firstDayOfYearOpeningPrice = yearPrice.get(CLOSING);
		}
	}

	private LocalDate getFirstDayAdjusted(DateUnit dateUnit) {
		LocalDate date = requestTime.getFirstDayOf(dateUnit);
		switch (dateUnit) {
		case WEEK:
			return (DayOfWeek.MONDAY.equals(date.getDayOfWeek())) ? subtractOneDay(date) : date;
		case MONTH:
			return (1 == date.getDayOfMonth()) ? subtractOneDay(date) : date;
		case YEAR:
			return (1 == date.getDayOfYear()) ? subtractOneDay(date) : date;
		default:
			return date;
		}
	}

	private LocalDate subtractOneDay(LocalDate date) {
		return date.minus(1L, ChronoUnit.DAYS);
	}

	@Override
	public boolean isSuitableForBuyOrder() {
		marketPredicate.validateBuyPredicateSetting();
		return marketPredicate.constructBuyPredicate().test(this);
	}

	@Override
	public boolean isSuitableForSellOrder() {
		throw new UnsupportedOperationException("selling is not checked.");
	}
}
