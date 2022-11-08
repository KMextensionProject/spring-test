package sk.golddigger.controllers;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.enums.ContentType;
import sk.golddigger.services.SchedulerSwitchService;

@Controller
public class SchedulerSwitchController {

	@Autowired
	private SchedulerSwitchService schedulerSwitchService;

	@PostMapping(path = "/scheduler/switch", produces = ContentType.APPLICATION_JSON)
	@SchemaLocation(outputPath = "schemas/out/post/toggleSwitch.json")
	@ResponseBody
	public Map<String, Object> toggleSwitch(HttpServletRequest request) {
		return schedulerSwitchService.toggleSwitch(request);
	}

}
