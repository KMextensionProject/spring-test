package sk.abstract_interface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * dopisat podla coinbase api
 * @author martom
 *
 */
public abstract class ExchangeAccount implements Refreshable {

	protected String accountId;

	@Autowired
	@Qualifier("accountCurrency")
	protected Currency accountCurrency;

	@Autowired
	@Qualifier("tradingCurrency")
	protected Currency tradingCurrency;

	protected double balance;

	protected double bestOrderBuyRate;

	public abstract void placeBuyOrder(Currency currency, double amount);

	public abstract void placeSellOrder(Currency currency, double amount);

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
		return this.balance;
	}

	public double getBestOrderBuyRate() {
		return this.bestOrderBuyRate;
	}
}
