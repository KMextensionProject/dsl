package sk.test.dsl.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import sk.test.dsl.services.DSLService;
import sk.test.dsl.store.StoreEnum;

@Controller
public class DSLController {

	@Autowired
	private DSLService service;
	
	public void nieco() {
//		service.getDiscountProducts("category", "path variable kaufland, lidl, tesco");
	}
}
