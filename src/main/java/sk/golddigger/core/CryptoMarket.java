package sk.golddigger.core;

import static sk.golddigger.enums.PriceType.CLOSING;
import static sk.golddigger.enums.PriceType.OPENING;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.core.RequestDateTime.DateUnit;
import sk.golddigger.enums.PriceType;
import sk.golddigger.http.CryptoMarketRequest;

@Component
public class CryptoMarket extends Market {

	private static final Logger logger = Logger.getLogger(CryptoMarket.class);

	@Autowired
	private CryptoMarketRequest cryptoMarketRequest;

	@Autowired
	private MarketPredicate marketPredicate;

	@Autowired
	private RequestDateTime requestTime;

	@Override
	public void updateState() throws Exception {
		this.currentPrice = cryptoMarketRequest.getCurrentPrice();
		updateOpeningAndClosingPrices();
	}

	// TODO: find a better way to express this
	private void updateOpeningAndClosingPrices() throws IOException {
		List<DateUnit> registeredDateUnits = marketPredicate.getRegisteredDateUnits();

		if (registeredDateUnits.contains(DateUnit.WEEK)) {
			Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.WEEK));
			this.firstDayOfWeekOpeningPrice = weekPrice.get(OPENING);
			this.firstDayOfWeekClosingPrice = weekPrice.get(CLOSING);
		}

		if (registeredDateUnits.contains(DateUnit.MONTH)) {
			Map<PriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.MONTH));
			this.firstDayOfMonthOpeningPrice = monthPrice.get(OPENING);
			this.firstDayOfMonthClosingPrice = monthPrice.get(CLOSING);
		}

		if (registeredDateUnits.contains(DateUnit.YEAR)) {
			Map<PriceType, Double> yearPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.YEAR));
			this.firstDayOfYearOpeningPrice = yearPrice.get(OPENING);
			this.firstDayOfYearOpeningPrice = yearPrice.get(CLOSING);
		}
	}

	@Override
	public boolean isSuitableForBuyOrder() {
		marketPredicate.validateBuyPredicateSetting();
		boolean suitableForBuyOrder = marketPredicate.constructBuyPredicate().test(this);
		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("predicateResult", suitableForBuyOrder));
		}
		return suitableForBuyOrder;
	}

	@Override
	public boolean isSuitableForSellOrder() {
		throw new UnsupportedOperationException("selling is not checked.");
	}
}
