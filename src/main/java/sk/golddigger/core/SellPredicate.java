package sk.golddigger.core;

import java.util.List;
import java.util.function.Predicate;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import sk.golddigger.core.RequestDateTime.DateUnit;

@Primary
@Component
public class SellPredicate extends MarketPredicate {

	static {
		System.out.println("loading other predicate");
	}
	
	@Override
	public void addPredicate(Predicate<Market> predicate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Predicate<Market> getUnderlyingPredicate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean testMarket(Market market) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DateUnit> getPredicatedDateUnits() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
