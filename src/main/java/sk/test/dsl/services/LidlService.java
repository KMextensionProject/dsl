package sk.test.dsl.services;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.product.parser.LidlURLMapper;
import sk.test.dsl.store.LidlStore;

@Service
public class LidlService {

	// tu sa oplati mat len jeden service
	// DSL -> pretoze uz tu vznikaju duplicitne kody len instancia je ina

	@Autowired
	private LidlURLMapper lidlMapper;

	@Autowired
	private LidlStore lidl;

	public Set<Category> getAvailableProductCategories() {
		return lidlMapper.getCategoryURLMap().keySet();
	}

	public List<DayOfWeek> getDiscountUpdatingDays() {
		return lidl.getDiscountUpdatingDays();
	}

	public List<Product> getDiscountProducts(String productCategory) {
		List<Product> discountProducts = lidl.getDiscountProducts();
		if (productCategory != null) {
			if (Category.contains(productCategory)) {
				Category category = Category.valueOf(productCategory.toUpperCase());
				return discountProducts.stream()
					.filter(e -> e.getCategory() == category)
					.collect(Collectors.toList());
			} else {
				return Collections.emptyList();
			}
		}
		return discountProducts;
	}

}
