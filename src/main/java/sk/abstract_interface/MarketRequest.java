package sk.abstract_interface;

import java.time.LocalDate;
import java.util.Map;

/**
 * kontrakt pre ziskanie dat trhu/ceny
 *
 */
public interface MarketRequest {

	public double getCurrentPrice() throws Exception;

	// come up with a better name
	public Map<PriceType, Double> getPricesByDate(LocalDate date) throws Exception;

}
