package sk.golddigger.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import sk.golddigger.services.MarketService;

@Controller
public class MarketController {

	@Autowired
	private MarketService marketService;


}
