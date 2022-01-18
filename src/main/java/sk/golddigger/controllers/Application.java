package sk.golddigger.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Application {

	@GetMapping(path = "/ping", produces = "text/plain")
	@ResponseBody
	public String ping() {
		return "Status OK";
	}

}
