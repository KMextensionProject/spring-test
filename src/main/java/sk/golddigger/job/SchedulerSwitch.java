package sk.golddigger.job;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;

public class SchedulerSwitch {

	private static final Logger logger = Logger.getLogger(SchedulerSwitch.class);

	private static boolean isActive = true;
	private static String switchOwner;
	private static long suspensionStart;
	
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
		suspensionStart = System.currentTimeMillis();
		logger.info(resolveMessage("jobSuspension", host));
	}

	private static void reActivate(String host) {
		if (switchOwner.equals(host)) {
			isActive = true;
			long durationSeconds = getSuspensionDurationInSeconds();
			logger.info(resolveMessage("jobReactivationWithTime", host, durationSeconds));
		} else {
			logger.warn(resolveMessage("jobReactivationError", host));
		}
	}

	private static long getSuspensionDurationInSeconds() {
		return ((System.currentTimeMillis() - suspensionStart) / 1000) / 60;
	}

	public static boolean isSwitchedOn() {
		return isActive;
	}

	public static String getSwitchOwner() {
		return switchOwner;
	}
}
