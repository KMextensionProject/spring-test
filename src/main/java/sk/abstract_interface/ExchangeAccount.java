package sk.abstract_interface;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * dopisat podla coinbase api
 * @author martom
 *
 */
public abstract class ExchangeAccount implements Refreshable {

	protected String accountId;

	@Autowired
	protected Currency currency;

	protected double balance;

	protected double bestOrderBuyRate;

	public abstract void placeBuyOrder(Currency currency, double amount);

	public abstract void placeSellOrder(Currency currency, double amount);

	public String getAccountId() {
		return this.accountId;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public double getBalance() {
		return this.balance;
	}

	public double getBestOrderBuyRate() {
		return this.bestOrderBuyRate;
	}
}
