package sk.implementation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.abstract_interface.Market;
import sk.abstract_interface.PriceType;

@Component
public class CryptoMarket extends Market {

	@Autowired
	private CryptoMarketRequest cryptoMarketRequest;

	@Override
	public void updateState() throws Exception {
		this.currentPrice = cryptoMarketRequest.getCurrentPrice();
		
		// zmenit v modely nazov premennej pre opening a closing price podla toho co to naozaj je
		// otvaracia cena zaciatku tyzdna, uzatvaracia cena zaciatku tyzdna..

		// ak je dnes pondelok, tak polygon bude tento request zdrziavat! zvolit nedelu - zatvaraciu cenu?
		Map<PriceType, Double> weekPrice = cryptoMarketRequest.getPricesByDate(LocalDate.now().with(DayOfWeek.MONDAY));
//		Map<PriceType, Double> weekPrice = bitcoinRequest.getPricesByDate(LocalDate.now().minusDays(1L));
		this.weekOpeningPrice = weekPrice.get(PriceType.OPENING);
		this.weekClosingPrice = weekPrice.get(PriceType.CLOSING);

		Map<PriceType, Double> monthPrice = cryptoMarketRequest.getPricesByDate(LocalDate.now().withDayOfMonth(1));
		this.monthOpeningPrice = monthPrice.get(PriceType.OPENING);
		this.monthClosingPrice = monthPrice.get(PriceType.CLOSING);
		// ...
	}

}