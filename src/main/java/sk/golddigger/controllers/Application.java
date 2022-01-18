package sk.golddigger.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Application {

	@GetMapping(path = "/ping", produces = "text/plain")
	public String ping() {
		return "Status OK";
	}

}
