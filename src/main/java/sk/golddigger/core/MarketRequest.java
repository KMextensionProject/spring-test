package sk.golddigger.core;

import java.time.LocalDate;
import java.util.Map;

import sk.golddigger.enums.MarketPriceType;

/**
 * kontrakt pre ziskanie dat trhu/ceny
 *
 */
public interface MarketRequest {

	public double getCurrentPrice();

	public double getAllTimeHigh();

	// come up with a better name
	public Map<MarketPriceType, Double> getPricesByDate(LocalDate date);

}
