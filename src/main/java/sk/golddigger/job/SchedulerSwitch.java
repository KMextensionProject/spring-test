package sk.golddigger.job;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import org.apache.log4j.Logger;

public class SchedulerSwitch {

	private static final Logger logger = Logger.getLogger(SchedulerSwitch.class);

	private static boolean isActive = true;
	private static String hostInControl;

	private SchedulerSwitch() {
		throw new IllegalStateException(resolveMessage("factoryClassInstantiationError", SchedulerSwitch.class));
	}

	public static void toggleSwitch(String host) {
		synchronized (SchedulerSwitch.class) {
			if (isActive) {
				hostInControl = host;
				isActive = false;
				logger.info("Host " + host + " suspended the scheduler.");
			} else {
				if (hostInControl.equals(host)) {
					hostInControl = null;
					isActive = true;
					logger.info("Host " + host + " reactivated the scheduler.");
				} else {
					logger.warn("Host " + host + " cannot reactivate the scheduled task because " + hostInControl + " is the owner.");
				}
			}
		}
	}

	public static boolean isSwitchedOn() {
		return isActive;
	}

	public static String getHostInControl() {
		return hostInControl;
	}
}
