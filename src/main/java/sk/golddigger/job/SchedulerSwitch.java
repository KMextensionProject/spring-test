package sk.golddigger.job;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;

public class SchedulerSwitch {

	private static final Logger logger = Logger.getLogger(SchedulerSwitch.class);

	private static boolean isActive = true;
	private static String switchOwner;

	private SchedulerSwitch() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", SchedulerSwitch.class));
	}

	public static void toggleSwitch(String host) {
		synchronized (SchedulerSwitch.class) {
			if (isActive) {
				suspend(host);
			} else {
				reActivate(host);
			}
		}
	}

	private static void suspend(String host) {
		switchOwner = host;
		isActive = false;
		logger.info(resolveMessage("jobSuspension", host));
	}

	private static void reActivate(String host) {
		if (switchOwner.equals(host)) {
			switchOwner = null;
			isActive = true;
			logger.info(resolveMessage("jobReactivationWithTime", host));
		} else {
			logger.warn(resolveMessage("jobReactivationError", host));
		}
	}

	public static boolean isSwitchedOn() {
		return isActive;
	}

	public static String getSwitchOwner() {
		return switchOwner;
	}
}
