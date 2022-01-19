package sk.golddigger.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.golddigger.services.SchedulerSwitchService;

@Controller
public class SchedulerSwitchController {

	@Autowired
	private SchedulerSwitchService schedulerSwitchService;

	@PostMapping(path = "/scheduler/switch", produces = "text/plain")
	@ResponseBody
	public String toggleSwitch(HttpServletRequest request) {
		return schedulerSwitchService.toggleSwitch(request);
	}

}
