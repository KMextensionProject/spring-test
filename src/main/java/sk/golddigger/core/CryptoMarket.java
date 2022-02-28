package sk.golddigger.core;

import static sk.golddigger.enums.PriceType.CLOSING;
import static sk.golddigger.enums.PriceType.OPENING;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.time.LocalDate;
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
	private RequestDateTime requestTime;

	@Override
	public void updateState() {
		this.currentPrice = cryptoMarketRequest.getCurrentPrice();
		updateAllTimeHigh();
		updateOpeningAndClosingPrices();

		logMarketPrices();
	}

	private void updateAllTimeHigh() {
		if (this.currentPrice > this.allTimeHigh) {
			this.allTimeHigh = cryptoMarketRequest.getAllTimeHigh();
			this.lastUpdatedAllTimeHigh = requestTime.getLocalDateUTC();
		}
	}

	private void updateOpeningAndClosingPrices() {
		LocalDate today = requestTime.getLocalDateUTC();

		if (!today.equals(lastUpdated)) {

			Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.WEEK));
			this.firstDayOfWeekOpeningPrice = weekPrice.get(OPENING);
			this.firstDayOfWeekClosingPrice = weekPrice.get(CLOSING);

			Map<PriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.MONTH));
			this.firstDayOfMonthOpeningPrice = monthPrice.get(OPENING);
			this.firstDayOfMonthClosingPrice = monthPrice.get(CLOSING);

			Map<PriceType, Double> yearPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.YEAR));
			this.firstDayOfYearOpeningPrice = yearPrice.get(OPENING);

			lastUpdated = today;
		}
	}

	private void logMarketPrices() {
		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("weekPrices", firstDayOfWeekOpeningPrice, firstDayOfWeekClosingPrice));
			logger.debug(resolveMessage("monthPrices", firstDayOfMonthOpeningPrice, firstDayOfMonthClosingPrice));
			logger.debug(resolveMessage("yearPrices", firstDayOfYearOpeningPrice));
		}
	}
}
