package sk.test.dsl.services;

import static java.util.stream.Collectors.toList;
import static sk.test.dsl.utils.StringUtils.containsPhrase;
import static sk.test.dsl.utils.StringUtils.stripDiacritics;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.store.KauflandStore;
import sk.test.dsl.store.LidlStore;
import sk.test.dsl.store.Store;
import sk.test.dsl.store.StoreName;
import sk.test.dsl.store.TescoStore;

@Service
public class DSLService {

	@Autowired
	private KauflandStore kaufland;

	@Autowired
	private TescoStore tesco;

	@Autowired
	private LidlStore lidl;

	public Set<Category> getAvailableProductCategories(StoreName storeName) {
		return getCorrespondingStoreInstance(storeName)
			.getUrlMapper()
			.getCategoryURLMap()
			.keySet();
	}

	public List<DayOfWeek> getDiscountUpdatingDays(StoreName storeName) {
		return getCorrespondingStoreInstance(storeName).getDiscountUpdatingDays();
	}

	public List<Product> getDiscountProducts(StoreName storeName, Category category, String name) {
		List<Product> discountProducts = getCorrespondingStoreInstance(storeName).getDiscountProducts();
		if (category == null && name == null) {
			return discountProducts;
		}
		return turnProductQueryToStream(discountProducts, category, name)
			.collect(toList());
	}

	private Stream<Product> turnProductQueryToStream(List<Product> products, Category category, String name) {
		Stream<Product> productStream = products.stream();
		if (category != null) {
			productStream = productStream.filter(e -> e.getCategory() == category);
		}
		if (name != null) {
			productStream = productStream.filter(e -> hasMatchInProductName(e, name));
		}
		return productStream;
	}

	private boolean hasMatchInProductName(Product product, String name) {
		String productFullName = stripDiacritics(product.getName());
		String lookupNamePart = stripDiacritics(name);
		return containsPhrase(productFullName, lookupNamePart);
	}

	private Store getCorrespondingStoreInstance(StoreName store) {
		switch(store) {
		case KAUFLAND:
			return kaufland;
		case TESCO:
			return tesco;
		case LIDL:
			return lidl;
		default:
			throw new IllegalArgumentException("store mus be one of " + StoreName.values());
		}
	}
}
