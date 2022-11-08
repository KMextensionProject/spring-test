package sk.golddigger.services;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import sk.golddigger.job.SchedulerSwitch;

@Service
public class SchedulerSwitchService {

	public Map<String, Object> toggleSwitch(HttpServletRequest request) {
		String host = request.getRemoteHost();

		SchedulerSwitch.toggleSwitch(host);
		String controlOwner = SchedulerSwitch.getSwitchOwner();

		Map<String, Object> response = new HashMap<>();
		response.put("control_owner", controlOwner);
		
		if (SchedulerSwitch.isSwitchedOn()) {
			response.put("action", "Reactivation");
			return response;
		}
		response.put("action", "Suspension");
		return response;
	}

}
