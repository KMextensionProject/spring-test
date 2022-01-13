package sk.golddigger.core;

import static sk.golddigger.utills.MessageResolver.resolveMessage;

import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.enums.Currency;
import sk.golddigger.http.CoinbaseRequest;

@Component
public class CoinbaseAccount extends ExchangeAccount {

	private static final Logger logger = Logger.getLogger(CoinbaseAccount.class);

	@Autowired
	private CoinbaseRequest accountRequest;

	@Autowired
	private AccountCache accountCache;

	@Override
	public void placeSellOrder(Currency currency, double amount) {
		throw new IllegalStateException();
	}

	@Override
	public void placeBuyOrder(Currency currency, double amount) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateState() throws Exception {
		balance = accountRequest.getAccountBalance(accountId);
	}

	@PostConstruct
	private void initAccountState() throws Exception {
		accountId = accountCache.getAccountIdByCurrency(accountCurrency);
		bestOrderBuyRate = computeThisYearBestOrderBuyRate();
		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("initialBestBuyRate", bestOrderBuyRate, 
					accountCurrency.getAcronym(), tradingCurrency.getAcronym()));
		}
	}

	private double computeThisYearBestOrderBuyRate() throws Exception {
		double price = 0;
		int year = Year.now().getValue();
		List<Map<String, Object>> fills = accountRequest.getAllOrderFills();

		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("accountFills", accountCurrency.getName(), fills));
		}

		if (!fills.isEmpty()) {
			Optional<Double> optionalPrice = fills.stream()
				.filter(e -> hasBeenFilledInYear(e, year))
				.filter(this::isBuyOrder)
				.map(this::getFillRate)
				.min(Comparator.comparing(Double::doubleValue));

			if (optionalPrice.isPresent()) {
				price = optionalPrice.get();
			}
		}

		return price;
	}

	private double getFillRate(Map<String, Object> fill) {
		return Double.parseDouble(String.valueOf(fill.get("price")));
	}

	private boolean isBuyOrder(Map<String, Object> fill) {
		return String.valueOf(fill.get("side")).equals("buy");
	}

	private boolean hasBeenFilledInYear(Map<String, Object> fill, int year) {
		return ZonedDateTime.parse(String.valueOf(fill.get("created_at"))).getYear() == year;
	}
}
