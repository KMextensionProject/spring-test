package sk.golddigger.services;

import static sk.golddigger.utils.MessageResolver.resolveMessage;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import sk.golddigger.job.SchedulerSwitch;

@Service
public class SchedulerSwitchService {

	public String toggleSwitch(HttpServletRequest request) {
		String host = request.getRemoteHost();

		SchedulerSwitch.toggleSwitch(host);
		String controlOwner = SchedulerSwitch.getSwitchOwner();

		if (SchedulerSwitch.isSwitchedOn()) {
			return resolveMessage("jobReactivation", controlOwner);
		}
		return resolveMessage("jobSuspension", controlOwner);
	}

}
