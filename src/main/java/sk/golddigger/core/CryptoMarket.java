package sk.golddigger.core;

import static sk.golddigger.core.RequestDateTime.DateUnit.MONTH;
import static sk.golddigger.core.RequestDateTime.DateUnit.WEEK;
import static sk.golddigger.core.RequestDateTime.DateUnit.YEAR;
import static sk.golddigger.enums.MarketPriceType.CLOSING;
import static sk.golddigger.enums.MarketPriceType.OPENING;
import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.time.LocalDate;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.enums.MarketPriceType;
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

	/**
	 * <p>This method is temporarily marked with <code>synchronized</code> keyword,
	 * in case of concurrent calls by the scheduler first delay and user interaction
	 * with UI in the start of the application. It may happen due to polygon API
	 * limitations on requests.</p>
	 */
	private synchronized void updateOpeningAndClosingPrices() {
		LocalDate today = requestTime.getLocalDateUTC();

		if (!today.equals(lastUpdated)) {
			logger.info("Updating market opening and closing prices");

			Map<MarketPriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(WEEK));
			this.firstDayOfWeekOpeningPrice = weekPrice.get(OPENING);
			this.firstDayOfWeekClosingPrice = weekPrice.get(CLOSING);

			Map<MarketPriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(MONTH));
			this.firstDayOfMonthOpeningPrice = monthPrice.get(OPENING);
			this.firstDayOfMonthClosingPrice = monthPrice.get(CLOSING);

			Map<MarketPriceType, Double> yearPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(YEAR));
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
