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
import sk.golddigger.exceptions.UnsupportedConfiguration;

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

	@Scheduled(initialDelayString = "${scheduler.initial_task_delay}", fixedRateString = "${scheduler.fixed_task_rate}")
	public void scheduledAction() {

		if (SchedulerSwitch.isSwitchedOn()) {
			account.updateState();

			if (account.getBalance() > 1) {
				market.updateState();

				boolean isSuitableForBuyOrder = buyPredicate.testMarket(market);
				if (isSuitableForBuyOrder) {

					logger.info("Is market suitable for buy order: " + isSuitableForBuyOrder);
					String productId = createProductId();

					Order buyOrder = new Order.OrderCreator()
							.setType(OrderType.MARKET)
							.setProductId(productId)
							.setFunds(1_000.00)
							.setSide(Side.BUY)
							.createOrder();

					logger.info("Placing buy order..");
					account.placeOrder(buyOrder);

					account.updateBestOrderBuyRate(market.getCurrentPrice());

					String tradingAccountId = accountCache.getAccountIdByCurrency(account.getTradingCurrency());
					double tradingBalance = exchangeRequest.getAccountBalance(tradingAccountId);
					logger.info("New state on trading account: " + tradingBalance);

					account.updateState();
					logger.info("Main account balance: " + account.getBalance());
					logger.info("The best filled buy order rate: " + account.getBestOrderBuyRate()
						+ account.getAccountCurrency().getAcronym());
				}
			}
		}
	}

	private String createProductId() {
		return account.getTradingCurrency().getAcronym() + "-" + account.getAccountCurrency().getAcronym();
	}

	@PostConstruct
	private void validateSchedulerTimingConfiguration() {
		validateTimingPresence();
		validateTimingContent();
		logger.info(resolveMessage("scheduledTaskInit", initDelay, fixedRate));
	}

	private void validateTimingPresence() {
		if ((initDelay.equals("null") || initDelay.isEmpty()) 
				|| (fixedRate.equals("null") || fixedRate.isEmpty())) {

			String schedulerTimeAbsence = resolveMessage("schedulerTimeAbsence");
			logger.error(schedulerTimeAbsence);
			throw new UnsupportedConfiguration(schedulerTimeAbsence);
		}
	}

	private void validateTimingContent() {
		if (!(StringUtils.isNumeric(initDelay) && StringUtils.isNumeric(fixedRate))) {
			String schedulerTimeError = resolveMessage("schedulerTimeError");
			logger.error(schedulerTimeError);
			throw new UnsupportedConfiguration(schedulerTimeError);
		}
	}
}
