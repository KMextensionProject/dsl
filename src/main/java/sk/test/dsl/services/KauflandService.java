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
import sk.test.dsl.product.parser.KauflandURLMapper;
import sk.test.dsl.store.KauflandStore;

@Service
public class KauflandService {

	@Autowired
	private KauflandURLMapper kauflandMapper;

	@Autowired
	private KauflandStore kaufland;

	public Set<Category> getAvailableProductCategories() {
		return kauflandMapper.getCategoryURLMap().keySet();
	}

	public List<DayOfWeek> getDiscountUpdatingDays() {
		return kaufland.getDiscountUpdatingDays();
	}

	public List<Product> getDiscountProducts(String productCategory) {
		List<Product> discountProducts = kaufland.getDiscountProducts();
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
