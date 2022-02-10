package sk.golddigger.job;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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

			double accountBalance = 1000;

			// because exchange accounts always have some fraction present
			if (accountBalance > 1) {
				market.updateState();

				if (isConvenientToBuy()) {
					String orderId = placeBuyOrder();
					double orderRate = getOrderRateById(orderId);
					account.updateBestOrderBuyRate(orderRate);
					account.updateState();

					sendNotification(accountBalance, orderRate);
				}
			}
		}
	}

	private boolean isConvenientToBuy() {
		return buyPredicate.testMarket(market) ? true : true;
	}

	private String placeBuyOrder() {
		logger.info("Market meets configured conditions. Placing buy order...");
		String productId = createProductId();
		createBuyOrder(productId);

		return "ce4e148b-38d0-4ce4-8564-454cdd9d6bce";//account.placeOrder(buyOrder);
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

	private double getOrderRateById(String orderId) {
		List<Map<String, Object>> fills = exchangeRequest.getAllOrderFills();

		Optional<Map<String, Object>> fill = fills.stream()
				.filter(e -> e.get("order_id").equals(orderId))
				.findFirst();

		if (fill.isPresent()) {
			return Double.valueOf(String.valueOf(fill.get().get("price")));
		}

		return 0.00;
	}

	private void sendNotification(double depositAmount, double depositRate) {
		String messageBody = constructNotificationMessageBody(depositAmount, depositRate);
		Message message = new Message("New buy order has been placed", messageBody);
		notification.send(message, recipient);
	}

	private String constructNotificationMessageBody(double depositAmount, double depositRate) {
		String tradingAccountId = accountCache.getAccountIdByCurrency(account.getTradingCurrency());
		double tradingBalance = exchangeRequest.getAccountBalance(tradingAccountId);

		StringBuilder messageBody = new StringBuilder();
		messageBody.append("Order amount: " + depositAmount + getAccountCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("Order rate: " + depositRate + getAccountCurrencyAcronym() + "/" + getTradingCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("Current trading account balance: " + 0.0000 + getTradingCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("Current main account balance: " + account.getBalance() + getAccountCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("The best filled buy order rate: " + account.getBestOrderBuyRate() + getAccountCurrencyAcronym());

		if (logger.isDebugEnabled()) {
			logger.debug(messageBody);
		}

		return messageBody.toString();
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
