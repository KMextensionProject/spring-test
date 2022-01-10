package sk.implementation;

import static sk.abstract_interface.PriceType.OPENING;
import static sk.abstract_interface.PriceType.CLOSING;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.abstract_interface.Market;
import sk.abstract_interface.PriceType;
import sk.bl.MarketPredicate;

@Component
public class CryptoMarket extends Market {

	@Autowired
	private CryptoMarketRequest cryptoMarketRequest;

	@Autowired
	private MarketPredicate marketPredicate;

	@Override
	public void updateState() throws Exception {
		this.currentPrice = cryptoMarketRequest.getCurrentPrice();

		// TODO:
		// ak je dnes pondelok, tak polygon bude tento request zdrziavat! zvolit nedelu - zatvaraciu cenu?
//		Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(LocalDate.now().with(DayOfWeek.MONDAY));
		Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(LocalDate.now().minusDays(1L));
		this.weekOpeningPrice = weekPrice.get(OPENING);
		this.weekClosingPrice = weekPrice.get(CLOSING);

		Map<PriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(LocalDate.now().withDayOfMonth(1));
		this.monthOpeningPrice = monthPrice.get(OPENING);
		this.monthClosingPrice = monthPrice.get(CLOSING);

		Map<PriceType, Double> yearPrice = cryptoMarketRequest.getPricesByDate(LocalDate.now().withDayOfYear(1));
		this.yearOpeningPrice = yearPrice.get(OPENING);
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
