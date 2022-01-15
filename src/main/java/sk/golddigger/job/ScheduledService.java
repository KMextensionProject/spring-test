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
import sk.golddigger.enums.Currency;
import sk.golddigger.exceptions.UnsupportedConfiguration;

@Component // TODO: make this a service when the project type changes to webapp
public class ScheduledService {

	private static final Logger logger = Logger.getLogger(ScheduledService.class);

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
	private MarketPredicate marketPredicate;

	@Autowired
	private ExchangeAccount account;

	@Autowired
	private AccountCache accountCache;

	@Autowired
	private ExchangeRequest exchangeRequest;

	// TODO: validate the user input
	@Scheduled(initialDelayString = "${scheduler.initial_task_delay}", fixedRateString = "${scheduler.fixed_task_rate}")
	public void scheduledAction() throws Exception {

		account.updateState();

		if (account.getBalance() > 1) {

			market.updateState();

			// come up with better name
			if (marketPredicate.testMarket()) {

				// i can buy whatever and not what i have configured? check it out.. i think only balance is enough
				account.placeBuyOrder(account.getBalance());
				
				String tradingAccountId = accountCache.getAccountIdByCurrency(account.getTradingCurrency());
				double balance = exchangeRequest.getAccountBalance(tradingAccountId);
				logger.info("my " + account.getTradingCurrency().getName() + " account has " + balance + account.getTradingCurrency().getAcronym());
				
				balance = exchangeRequest.getAccountBalance(account.getAccountId());
				logger.info("my " + account.getAccountCurrency().getName() + " account has " + balance + account.getAccountCurrency().getAcronym());
			}
		}
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
