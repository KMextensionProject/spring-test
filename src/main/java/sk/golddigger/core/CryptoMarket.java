package sk.golddigger.core;

import static sk.golddigger.enums.PriceType.CLOSING;
import static sk.golddigger.enums.PriceType.OPENING;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.core.RequestDateTime.DateUnit;
import sk.golddigger.enums.PriceType;
import sk.golddigger.http.CryptoMarketRequest;

@Component
public class CryptoMarket extends Market {

	@Autowired
	private CryptoMarketRequest cryptoMarketRequest;

	@Autowired
	private RequestDateTime requestTime;

	@Override
	public void updateState() throws Exception {
		this.currentPrice = cryptoMarketRequest.getCurrentPrice();
		updateOpeningAndClosingPrices();
	}

	// TODO: find a better way to express this
	private void updateOpeningAndClosingPrices() throws IOException {

		Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.WEEK));
		this.firstDayOfWeekOpeningPrice = weekPrice.get(OPENING);
		this.firstDayOfWeekClosingPrice = weekPrice.get(CLOSING);

		Map<PriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.MONTH));
		this.firstDayOfMonthOpeningPrice = monthPrice.get(OPENING);
		this.firstDayOfMonthClosingPrice = monthPrice.get(CLOSING);

		Map<PriceType, Double> yearPrice = cryptoMarketRequest.getPricesByDate(requestTime.getFirstDayAdjusted(DateUnit.YEAR));
		this.firstDayOfYearOpeningPrice = yearPrice.get(OPENING);
		this.firstDayOfYearOpeningPrice = yearPrice.get(CLOSING);
	}

}
