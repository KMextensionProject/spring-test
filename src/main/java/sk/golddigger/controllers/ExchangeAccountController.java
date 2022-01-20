package sk.golddigger.controllers;

import static sk.golddigger.http.ContentType.APPLICATION_JSON;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import sk.golddigger.services.ExchangeAccountService;

@Controller
public class ExchangeAccountController {

	@Autowired
	private ExchangeAccountService exchangeService;

	@GetMapping(path = "/account/complexOverview", produces = APPLICATION_JSON)
	public Map<String, Object> getAccountComplexOverview() {
		return exchangeService.getAccountComplexOverview();
	}

}
