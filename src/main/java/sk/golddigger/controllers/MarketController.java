package sk.golddigger.controllers;

import static sk.golddigger.enums.ContentType.APPLICATION_JSON;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.golddigger.annotations.SchemaLocation;
import sk.golddigger.services.MarketService;

@Controller
public class MarketController {

	@Autowired
	private MarketService marketService;

	@GetMapping(path = "/market/complexOverview", produces = APPLICATION_JSON)
	@SchemaLocation(outputPath = "")
	@ResponseBody
	public Map<String, Object> getMarketComplexOverview() {
		return marketService.getMarketComplexOverview();
	}
}
