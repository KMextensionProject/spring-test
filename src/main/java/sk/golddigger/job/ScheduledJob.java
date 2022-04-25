package sk.golddigger.job;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import java.time.LocalDate;
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
import sk.golddigger.coinbase.CoinbaseOrderConstants.OrderType;
import sk.golddigger.coinbase.CoinbaseOrderConstants.Side;
import sk.golddigger.core.ExchangeAccount;
import sk.golddigger.core.ExchangeRequest;
import sk.golddigger.core.Market;
import sk.golddigger.core.MarketPredicate;
import sk.golddigger.core.Order;
import sk.golddigger.core.RequestDateTime;
import sk.golddigger.messaging.Message;
import sk.golddigger.messaging.Recipient;
import sk.golddigger.notification.Notification;
import sk.golddigger.utils.TypeUtils;

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

	@Autowired
	private RequestDateTime requestDateTime;

	private LocalDate lastAthNotificationDate;

	@Scheduled(initialDelayString = "${scheduler.initial_task_delay}", fixedRateString = "${scheduler.fixed_task_rate}")
	public void scheduledAction() {

		account.updateState();
		market.updateState();

		checkAndSendAthNotification();

		if (SchedulerSwitch.isSwitchedOn()) {
			double accountBalance = account.getBalance();

			// exchange accounts always have some fraction present
			if (accountBalance > 1 && isConvenientToBuy()) {

				String orderId = placeBuyOrder();
				double orderRate = getOrderRateById(orderId);
				account.updateBestOrderBuyRate(orderRate);
				account.updateState();

				sendOrderNotification(accountBalance, orderRate);
			}
		}
	}

	// It's enough to be notified once a day in case of ATH breakthrough.
	private void checkAndSendAthNotification() {
		LocalDate today = requestDateTime.getLocalDateUTC();
		if (!today.equals(lastAthNotificationDate) && today.equals(market.getLastUpdatedAllTimeHigh())) {
			sendAllTimeHighNotification();
			lastAthNotificationDate = today;
		}
	}

	private void sendAllTimeHighNotification() {
		if (!isRecipientDefined()) {
			return;
		}
		StringBuilder athMessageBody = new StringBuilder();
		athMessageBody.append("New all time high has just been reached at ");
		athMessageBody.append(market.getAllTimeHigh() + " ");
		athMessageBody.append(getAccountCurrencyAcronym() + "/");
		athMessageBody.append(getTradingCurrencyAcronym());

		Message athMessage = new Message("Gold Digger - New ATH has been reached", athMessageBody.toString());
		notification.send(athMessage, recipient);

		if (logger.isDebugEnabled()) {
			logger.debug(athMessageBody);
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

	private double getOrderRateById(String orderId) {
		List<Map<String, Object>> orderFills = exchangeRequest.getAllOrderFills();

		Optional<Map<String, Object>> orderFill = orderFills.stream()
			.filter(e -> e.get("order_id").equals(orderId))
			.findFirst();

		if (orderFill.isPresent()) {
			String orderRate = String.valueOf(orderFill.get().get("price"));
			return Double.parseDouble(orderRate);
		}

		return 0.00;
	}

	private void sendOrderNotification(double depositAmount, double orderRate) {
		if (!isRecipientDefined()) {
			return;
		}
		String messageBody = constructOrderNotificationMessageBody(depositAmount, orderRate);
		Message message = new Message("Gold Digger - New buy order has been placed", messageBody);
		notification.send(message, recipient);
	}

	private String constructOrderNotificationMessageBody(double depositAmount, double orderRate) {
		String tradingAccountId = accountCache.getAccountIdByCurrency(account.getTradingCurrency());
		double tradingBalance = exchangeRequest.getAccountBalance(tradingAccountId);

		Object bestBuyOrderRate = TypeUtils.getValueByCondition(e -> e > 0, account.getBestOrderBuyRate(), "No order has been placed in this year.");
		if(bestBuyOrderRate instanceof Number) {
			bestBuyOrderRate += getAccountCurrencyAcronym();
		}

		StringBuilder messageBody = new StringBuilder();
		messageBody.append("Order amount: " + depositAmount + " " + getAccountCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("Order rate: " + orderRate + getAccountCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("Current trading account balance: " + tradingBalance + getTradingCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("Current main account balance: " + account.getBalance() + getAccountCurrencyAcronym());
		messageBody.append(System.lineSeparator());
		messageBody.append("The best filled buy order rate: " + bestBuyOrderRate);

		if (logger.isDebugEnabled()) {
			logger.debug(messageBody);
		}

		return messageBody.toString();
	}

	private boolean isRecipientDefined() {
		boolean recipientPresent = recipient.isDefined();
		if (!recipientPresent) {
			logger.warn(resolveMessage("undefinedNotificationRecipient"));
		}
		return recipientPresent;
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

		this.lastAthNotificationDate = requestDateTime.getLocalDateUTC();
	}

	private void validateTimingPresence() {
		if (("null".equals(initDelay)) || ("null".equals(fixedRate))) {
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
