package sk.golddigger.coinbase;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

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
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.Order;
import sk.golddigger.exceptions.ApplicationFailure;
import sk.golddigger.exceptions.ClientSideFailure;
import sk.golddigger.http.CoinbaseRequest;

@Component
public class CoinbaseAccount extends ExchangeAccount {

	private static final Logger logger = Logger.getLogger(CoinbaseAccount.class);

	@Autowired
	private CoinbaseRequest accountRequest;

	@Autowired
	private AccountCache accountCache;

	@Override
	public String placeOrder(Order order) {
		return accountRequest.postOrder(order);
	}

	@Override
	public void updateState() {
		balance = accountRequest.getAccountBalance(accountId);
	}

	@PostConstruct
	private void initAccountState() {
		try {
			accountId = accountCache.getAccountIdByCurrency(accountCurrency);
			bestOrderBuyRate = computeThisYearBestOrderBuyRate();

			if (logger.isDebugEnabled()) {
				logger.debug(resolveMessage("initialBestBuyRate", bestOrderBuyRate, accountCurrency.getAcronym(),
						tradingCurrency.getAcronym()));
			}
		} catch (ApplicationFailure | ClientSideFailure failure) {
			logger.error(resolveMessage("accountInitError", failure.getMessage()));
			System.exit(1);
		}
	}

	private double computeThisYearBestOrderBuyRate() {
		double price = 0;
		int year = Year.now().getValue();
		List<Map<String, Object>> fills = accountRequest.getAllOrderFills();

		if (logger.isDebugEnabled()) {
			logger.debug(resolveMessage("accountFillsPayload", accountCurrency.getName(), fills));
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
