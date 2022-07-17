package sk.golddigger.controllers;

import static sk.golddigger.enums.ContentType.APPLICATION_JSON;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import sk.golddigger.services.ApplicationService;

@Controller
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@GetMapping(path = "/ping")
	@ResponseStatus(HttpStatus.OK)
	public void ping() { 
		// this method is only for checking whether the application is alive
	}

	@GetMapping(path = "/endpoints", produces = APPLICATION_JSON)
	@ResponseBody
	public Set<String> getAvailableEndpoints() {
		return applicationService.getAvailableEndpoints();
	}

	@GetMapping(path = "/home")
	public String home() {
		return "Home";
	}

	@PostMapping(path = "/account/cache/drop")
	@ResponseBody
	public void dropAccountCaches() {
		applicationService.dropAccountCaches();
	}
}
