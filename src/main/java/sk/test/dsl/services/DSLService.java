package sk.test.dsl.services;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static sk.test.dsl.utils.StringUtils.containsPhrase;
import static sk.test.dsl.utils.StringUtils.stripDiacritics;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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

// TODO: thing through and exchange for Pair object where map functionality is not needed

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

	public List<Map<String, List<Map<String, Object>>>> resolveDSL(List<String> shoppingList) {
		List<Map<String, List<Map<String, Object>>>> dsl = new ArrayList<>(shoppingList.size());

		for (String productName : shoppingList) {
			Map<String, List<Map<String, Object>>> productForDSL = new HashMap<>();
			Map<StoreName, List<Product>> foundProductByStore = searchStoresForProduct(productName);

			if (foundProductByStore.isEmpty()) {
				productForDSL.put(productName, null);
			} else if (isProductComparable(foundProductByStore)) {
				Map<StoreName, Product> bestOffer = getBestStoreOffer(getAsSingleProductMap(foundProductByStore));
				productForDSL.put(productName, toSingleProductListWithStoreElement(bestOffer));
			} else {
				List<Map<String, Object>> productMaps = toProductListWithStoreElement(foundProductByStore);
				productForDSL.put(productName, productMaps);
			}

			dsl.add(productForDSL);
		}

		return dsl;
	}

	private Map<StoreName, List<Product>> searchStoresForProduct(String productName) {
		Map<StoreName, List<Product>> productsFound = new HashMap<>();
		for (StoreName storeName : StoreName.values()) {
			List<Product> foundProducts = getDiscountProducts(storeName, null, productName);
			if (!foundProducts.isEmpty()) {
				productsFound.put(storeName, foundProducts);	
			}
		}
		return productsFound;
	}

	/*
	 * if we found only one product in each store, than there is possibility to pick the best offer 
	 * if there were multiple products found in one store for the searched value and others have 
	 * only one product...there is indication that the client request could be more specific and 
	 * so we are not able to pick the correct result
	 */
	private boolean isProductComparable(Map<StoreName, List<Product>> productsByStore) {
		Collection<List<Product>> foundProducts = productsByStore.values();
		if (foundProducts.size() == 1 && foundProducts.iterator().next().size() == 1) {
			return true;
		}
		int offersWithOneProduct = 0;
		for (List<Product> products : foundProducts) {
			if (products.size() == 1) {
				offersWithOneProduct++;
			}
		}
		return offersWithOneProduct == foundProducts.size();
	}

	private Map<StoreName, Product> getAsSingleProductMap(Map<StoreName, List<Product>> map) {
		return map.entrySet()
			.stream()
			.collect(toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
	}

	private Map<StoreName, Product> getBestStoreOffer(Map<StoreName, Product> foundProduct) {
		Map.Entry<StoreName, Product> bestOffer = Collections.min(foundProduct.entrySet(), Comparator.comparingDouble(e -> e.getValue().getPrice()));
		Map<StoreName, Product> multiplePotentialbestOffers = foundProduct.entrySet()
			.stream()
			.filter(e -> e.getKey() != bestOffer.getKey())
			.filter(e -> e.getValue().getPrice() == bestOffer.getValue().getPrice())
			.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		multiplePotentialbestOffers.put(bestOffer.getKey(), bestOffer.getValue());
		return multiplePotentialbestOffers;
	}

	private List<Map<String, Object>> toSingleProductListWithStoreElement(Map<StoreName, Product> bestOffer) {
		List<Map<String, Object>> productList = new ArrayList<>(1);

		String bestOfferStoreValue = bestOffer.keySet()
			.stream()
			.map(StoreName::name)
			.collect(Collectors.joining("/"));

		Map<String, Object> productMap = bestOffer.entrySet().iterator().next().getValue().toMap();
		productMap.put("best_offer_store", bestOfferStoreValue);
		productList.add(productMap);

		return productList;
	}

	private List<Map<String, Object>> toProductListWithStoreElement(Map<StoreName, List<Product>> productsByStore) {
		List<Map<String, Object>> productMaps = new ArrayList<>();
		for (Map.Entry<StoreName, List<Product>> product : productsByStore.entrySet()) {
			String store = product.getKey().name();
			productMaps.addAll(product.getValue()
				.stream()
				.map(Product::toMap)
				.map(p -> {
					p.put("store", store);
					return p;
				})
				.collect(toList()));
		}
		return productMaps;
	}

}
