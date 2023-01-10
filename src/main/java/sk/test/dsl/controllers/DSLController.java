package sk.test.dsl.controllers;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.services.DSLService;
import sk.test.dsl.store.StoreName;

@Controller
public class DSLController {

	private static final String APPLICATION_JSON = "application/json";

	@Autowired
	private DSLService service;

	@GetMapping(path = "/stores/", produces = APPLICATION_JSON)
	@ResponseBody
	public List<String> getStoreNames(){
		return service.getStoreNames();
	}

	@GetMapping(path = "/{store}/product/categories/", produces = APPLICATION_JSON)
	@ResponseBody
	public Set<Category> getAvailableProductCategories(@PathVariable StoreName store) {
		return service.getAvailableProductCategories(store);
	}

	@GetMapping(path = "/{store}/product/update-days/", produces = APPLICATION_JSON)
	@ResponseBody
	public List<DayOfWeek> getDiscountUpdatingDays(@PathVariable StoreName store) {
		return service.getDiscountUpdatingDays(store);
	}

	@GetMapping(path = "/{store}/product/", produces = APPLICATION_JSON)
	@ResponseBody
	public List<Product> getDiscountProducts(
			@PathVariable StoreName store, 
			@RequestParam(name = "category", required = false) Category category,
			@RequestParam(name = "productName", required = false) String productName) {
		return service.getDiscountProducts(store, category, productName);
	}

	@PostMapping(path = "/", consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
	@ResponseBody
	public List<Map<String, List<Map<String, Object>>>> resolveDSL(@RequestBody List<String> shoppingList) {
		return service.resolveDSL(shoppingList);
	}
	
	

}
