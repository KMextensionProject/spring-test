package sk.golddigger.controllers;

import static sk.golddigger.enums.ContentType.TEXT_PLAIN;
import static sk.golddigger.utils.MapUtils.toStringTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.golddigger.services.MarketService;

@Controller
public class MarketController {

	@Autowired
	private MarketService marketService;

	@GetMapping(path = "/market/complexOverview", produces = TEXT_PLAIN)
	@ResponseBody
	public String getMarketComplexOverview() {
		return toStringTree(marketService.getMarketComplexOverview());
	}
}
