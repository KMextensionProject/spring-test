package sk.golddigger.services;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import sk.golddigger.job.SchedulerSwitch;

@Service
public class SchedulerSwitchService {

	public String toggleSwitch(HttpServletRequest request) {
		String host = request.getRemoteHost();

		SchedulerSwitch.toggleSwitch(host);

		if (SchedulerSwitch.isSwitchedOn()) {
			return "Scheduled service has been switched on by " + SchedulerSwitch.getHostInControl();
		}
		return "Scheduled service has been suspended by " + SchedulerSwitch.getHostInControl();
	}

}
