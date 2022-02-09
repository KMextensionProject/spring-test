package sk.golddigger.job;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sk.golddigger.cache.AccountCache;
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.Market;
import sk.golddigger.core.MarketPredicate;
import sk.golddigger.core.Order;
import sk.golddigger.core.Order.OrderType;
import sk.golddigger.core.Order.Side;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;
import sk.golddigger.notification.Notification;

@Component
public class ScheduledJob {

	private static final Logger logger = Logger.getLogger(ScheduledJob.class);

/* THERSE TWO PROPERTIES ARE NEEDED FOR PURPOSE OF USER INPUT VALIDATION.
 * BECAUSE @Scheduled VALUES NEED TO BE CONSTANT EXPRESSION, THEY NEED
 * TO BE EXPRESSED AT COMPILE TIME OR INJECTED DIRECTLY AS BELOW.
 */
	@Value("${scheduler.initial_task_delay:null}")
	private String initDelay;

	@Value("${scheduler.fixed_task_rate:null}")
	private String fixedRate;

	@Autowired
	private Market market;

	@Autowired
	private MarketPredicate buyPredicate;

	@Autowired
	private ExchangeAccount account;

	@Autowired
	private AccountCache accountCache;

	@Autowired
	private ExchangeRequest exchangeRequest;

	@Autowired
	private Notification notification;

	@Autowired
	private Recipient recipient;

	@Scheduled(initialDelayString = "${scheduler.initial_task_delay}", fixedRateString = "${scheduler.fixed_task_rate}")
	public void scheduledAction() {

		if (SchedulerSwitch.isSwitchedOn()) {
			account.updateState();

			// because exchange accounts always have some fraction present
			if (account.getBalance() > 1) {
				market.updateState();

				if (isConvenientToBuy()) {
					placeBuyOrder();
					account.updateBestOrderBuyRate(market.getCurrentPrice());
					account.updateState();

					sendNotification();
				}
			}
		}
	}

	private boolean isConvenientToBuy() {
		return buyPredicate.testMarket(market);
	}

	private String placeBuyOrder() {
		logger.info("Market meets configured conditions. Placing buy order...");
		String productId = createProductId();
		Order buyOrder = createBuyOrder(productId);

		return account.placeOrder(buyOrder);
	}

	private String createProductId() {
		return account.getTradingCurrency().getAcronym() + "-" + account.getAccountCurrency().getAcronym();
	}

	private Order createBuyOrder(String productId) {
		return new Order.OrderCreator()
			.setType(OrderType.MARKET)
			.setProductId(productId)
			.setFunds(account.getBalance())
			.setSide(Side.BUY)
			.createOrder();
	}

	private void sendNotification() {
		String tradingAccountId = accountCache.getAccountIdByCurrency(account.getTradingCurrency());
		double tradingBalance = exchangeRequest.getAccountBalance(tradingAccountId);

		String newTradingAccountState = "New state on trading account: " + tradingBalance + getTradingCurrencyAcronym();
		String newMainAccountState = "Main account balance: " + account.getBalance() + getAccountCurrencyAcronym();
		String bestBuyOrderRate = "The best filled buy order rate: " + account.getBestOrderBuyRate() + getAccountCurrencyAcronym();

		logger.info(newTradingAccountState);
		logger.info(newMainAccountState);
		logger.info(bestBuyOrderRate);

		String messageBody = newTradingAccountState 
				+ System.lineSeparator() 
				+ newMainAccountState 
				+ System.lineSeparator() 
				+ bestBuyOrderRate;

		Message message = new Message("New buy order has been placed", messageBody);
		notification.send(message, recipient);
	}

	private String getAccountCurrencyAcronym() {
		return account.getAccountCurrency().getAcronym();
	}

	private String getTradingCurrencyAcronym() {
		return account.getTradingCurrency().getAcronym();
	}

	@PostConstruct
	private void validateSchedulerTimingConfiguration() {
		validateTimingPresence();
		validateTimingContent();
		logger.info(resolveMessage("scheduledTaskInit", initDelay, fixedRate));

		buyPredicate.addPredicate(m -> m.getCurrentPrice() < account.getBestOrderBuyRate());
		logger.info("Added buy predicate for the best order buy rate.");
	}

	private void validateTimingPresence() {
		if (("null".equals(initDelay) || initDelay.isEmpty()) 
				|| ("null".equals(fixedRate) || fixedRate.isEmpty())) {

			logger.error(resolveMessage("schedulerTimeAbsence"));
			System.exit(1);
		}
	}

	private void validateTimingContent() {
		if (!(StringUtils.isNumeric(initDelay) && StringUtils.isNumeric(fixedRate))) {
			logger.error(resolveMessage("schedulerTimeError"));
			System.exit(1);
		}
	}
}
