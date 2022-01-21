package sk.golddigger.controllers;

import static sk.golddigger.enums.ContentType.APPLICATION_JSON;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.golddigger.services.ExchangeAccountService;

@Controller
public class ExchangeAccountController {

	@Autowired
	private ExchangeAccountService exchangeService;

	@GetMapping(path = "/account/complexOverview", produces = APPLICATION_JSON)
	@ResponseBody
	public Map<String, Object> getAccountComplexOverview() {
		return exchangeService.getAccountComplexOverview();
	}

	@GetMapping(path = "/account/orders/?excel()")
	public void generateOrdersReportToExcel(HttpServletResponse response) {
		exchangeService.generateOrdersReportToExcel(response);
	}

}
