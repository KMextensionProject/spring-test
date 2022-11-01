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

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.services.ApplicationService;

@Controller
public class ApplicationController {

	@Autowired
	private ApplicationService applicationService;

	@GetMapping(path = "/ping")
	@ResponseStatus(HttpStatus.OK)
	@SchemaLocation(noSchema = true)
	public void ping() { 
		// this method is only for checking whether the application is alive
	}

	@GetMapping(path = "/endpoints", produces = APPLICATION_JSON)
	@SchemaLocation(noSchema = true)
	@ResponseBody
	public Set<String> getAvailableEndpoints() {
		return applicationService.getAvailableEndpoints();
	}

	@GetMapping(path = "/home")
	@SchemaLocation(noSchema = true)
	public String home() {
		return "Home";
	}

	@PostMapping(path = "/account/cache/drop")
	@SchemaLocation(noSchema = true)
	@ResponseBody
	public void dropAccountCaches() {
		applicationService.dropAccountCaches();
	}

	// example controller for payload validation test
//	@PostMapping(path = "/validation_sample", consumes = APPLICATION_JSON)
//	@SchemaLocation(inputPath = "schemas/POST_validation_sample.json")
//	@ResponseBody
//	public Map<String, Object> validationSample(@RequestBody Map<String, Object> data) {
//		System.out.println("We managed to get into a handler method with data: " + data);
//		data.clear();
//		data.put("input_validation_result", "OK");
//		return data;
//	}
}
