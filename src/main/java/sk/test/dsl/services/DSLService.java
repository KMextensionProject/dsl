package sk.test.dsl.services;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.store.KauflandStore;
import sk.test.dsl.store.LidlStore;
import sk.test.dsl.store.Store;
import sk.test.dsl.store.TescoStore;

@Service
public class DSLService {

	@Autowired
	private KauflandStore kaufland;

	@Autowired
	private TescoStore tesco;

	@Autowired
	private LidlStore lidl;

//	 store id will come from path variable and we could have only one controller class then
//	public Set<Category> getAvailableProductCategories(int storeId) {
//		return getURLMapper(storeId).getCategoryURLMap().keySet();
//	}

	public List<DayOfWeek> getDiscountUpdatingDays(Store store) {
		return store.getDiscountUpdatingDays();
	}

	public List<Product> getDiscountProducts(String productCategory, Store store) {
		List<Product> discountProducts = store.getDiscountProducts();
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
