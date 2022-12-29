package sk.test.dsl.controllers;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.services.LidlService;

@Controller
public class Lidl {

	@Autowired
	private LidlService service;

	@GetMapping(path = "/lidl/product/categories/", produces = "application/json")
	@ResponseBody
	public Set<Category> getAvailableProductCategories() {
		return service.getAvailableProductCategories();
	}

	@GetMapping(path = "/lidl/product/update-days/", produces = "application/json")
	@ResponseBody
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return service.getDiscountUpdatingDays();
	}

	@GetMapping(path = "/lidl/product/", produces = "application/json")
	@ResponseBody
	public List<Product> getDiscountProducts(@RequestParam(name = "category", required = false) String category) {
		return service.getDiscountProducts(category);
	}

}
