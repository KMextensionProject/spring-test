package sk.golddigger.controllers;

import static sk.golddigger.enums.ContentType.TEXT_PLAIN;
import static sk.golddigger.utils.MapUtils.toStringTree;

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

	@GetMapping(path = "/account/complexOverview", produces = TEXT_PLAIN)
	@ResponseBody
	public String getAccountComplexOverview() {
		return toStringTree(exchangeService.getAccountComplexOverview());
	}

	@GetMapping(path = "/account/orders/excel")
	public void generateOrdersReportToExcel(HttpServletResponse response) {
		exchangeService.generateOrdersReportToExcel(response);
	}

}
