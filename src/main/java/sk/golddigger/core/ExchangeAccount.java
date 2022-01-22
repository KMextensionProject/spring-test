package sk.golddigger.core;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import sk.golddigger.enums.Currency;

/**
 * 
 */
public abstract class ExchangeAccount implements Updatable {

	private static final Logger logger = Logger.getLogger(ExchangeAccount.class);

	protected String accountId;

	@Autowired
	@Qualifier("accountCurrency")
	protected Currency accountCurrency;

	@Autowired
	@Qualifier("tradingCurrency")
	protected Currency tradingCurrency;

	protected double balance;

	protected double bestOrderBuyRate;

	public abstract String placeOrder(Order order);

	public String getAccountId() {
		return this.accountId;
	}

	public Currency getAccountCurrency() {
		return this.accountCurrency;
	}

	public Currency getTradingCurrency() {
		return this.tradingCurrency;
	}

	public double getBalance() {
		return Math.floor(this.balance);
	}

	public void updateBestOrderBuyRate(final double newRate) {
		if (newRate < this.bestOrderBuyRate) {
			if (logger.isDebugEnabled()) {
				logger.debug(resolveMessage("newBestBuyRate", newRate,
						accountCurrency.getAcronym(), tradingCurrency.getAcronym()));
			}
			this.bestOrderBuyRate = newRate;
		}
	}

	public double getBestOrderBuyRate() {
		return this.bestOrderBuyRate;
	}

}
