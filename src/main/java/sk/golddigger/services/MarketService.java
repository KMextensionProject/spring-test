package sk.golddigger.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.golddigger.core.Market;
import sk.golddigger.core.MarketPredicate;

@Service
public class MarketService {

	@Autowired
	private Market market;

	@Autowired
	private MarketPredicate marketPredicate;


}
