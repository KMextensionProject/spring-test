package sk.golddigger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.golddigger.services.ApplicationService;

@Controller
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@GetMapping(path = "/ping", produces = "text/plain")
	@ResponseBody
	public String ping() {
		return applicationService.ping();
	}

}