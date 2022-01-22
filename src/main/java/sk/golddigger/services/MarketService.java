package sk.golddigger.services;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.Market;
import sk.golddigger.core.MarketPredicate;
import sk.golddigger.core.RequestDateTime.DateUnit;
import sk.golddigger.enums.Currency;

@Service
public class MarketService {

	@Autowired
	private Market market;

	@Autowired
	private MarketPredicate marketPredicate;

	@Autowired
	private ExchangeAccount account;

	public Map<String, Object> getMarketComplexOverview() {

		Currency accountCurrency = account.getAccountCurrency();
		Currency tradingCurrency = account.getTradingCurrency();

		market.updateState();

		Map<String, Object> marketData = new LinkedHashMap<>();
		marketData.put("market_currency", tradingCurrency.getName());
		marketData.put("conversion_currency", accountCurrency.getName());
		marketData.put("current_price", market.getCurrentPrice());
		marketData.put("ath", market.getAllTimeHigh());
		marketData.put("week_opening_price", market.getFirstDayOfWeekOpeningPrice());
		marketData.put("month_opening_price", market.getFirstDayOfMonthOpeningPrice());
		marketData.put("year_opening_price", market.getFirstDayOfYearOpeningPrice());
		marketData.put("market_predicate_setting", getMarketPredicateSetting());
		marketData.put("market_predicate_result", marketPredicate.testMarket(market));
		// TODO: add JSR-310 as project dependency
		marketData.put("last_updated", market.getLastUpdated().toString());

		return marketData;
	}

	private Map<String, Object> getMarketPredicateSetting() {
		Map<String, Object> marketPredicateSetting = new LinkedHashMap<>(3);

		List<DateUnit> dateUnits = marketPredicate.getPredicatedDateUnits();

		for (DateUnit dateUnit : dateUnits) {
			String key = dateUnit.toString().toLowerCase();
			int value = 0;

			switch(dateUnit) {
			case WEEK:
				value = marketPredicate.getWeekPredicatePercentage();
				break;
			case MONTH:
				value = marketPredicate.getMonthPredicatePercentage();
				break;
			case YEAR:
				value = marketPredicate.getYearPredicatePercentage();
			}

			marketPredicateSetting.put(key, value);
		}

		return marketPredicateSetting;
	}
}
