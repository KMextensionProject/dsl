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

	// TODO: prerobit tuto metodu a jej podmetody genericky, ked pridam 3 nove obchody, tak nech nemusim
	// na tomto mieste nic uz pisat
	public void resolveDSL(List<String> shoppingList) {

		// TODO: thing through and exchange for Pair object where map functionality is not needed
		List<Map<String, List<Map<String, Object>>>> dsl = new ArrayList<>(shoppingList.size());

		for (String productName : shoppingList) {

			Map<String, List<Map<String, Object>>> productForDSL = new HashMap<>();
			Map<StoreName, List<Product>> foundProductByStore = searchStoresForProduct(productName);

			if (foundProductByStore.isEmpty()) {
				// product is not found in any store's discount list
				productForDSL.put(productName, null);
				dsl.add(productForDSL);
				continue;
			}

			if (isProductComparable(foundProductByStore)) {
				Map<StoreName, Product> bestOffer = getBestStoreOffer(getAsSingleProductMap(foundProductByStore));
				// TODO: ak je tu viac obchodov co ma ten produkt, znamena to, ze maju rovnaku cenu a tu treba spravit aj to,
				// ze sa appendne nie len jeden obchod ako best ale oba...takze upravit tuto medotu dole, nech vrati jeden produkt
				// s popisom ze sa da kupit v oboch StoreName
				productForDSL.put(productName, toProductListWithStoreElement(bestOffer));
				dsl.add(productForDSL);
			} else {
				// ak maju toho v sebe viac, ze sa neda urcit kde je to vyhodne dame tam nie viac obchodov, ale nech sa na UI zobrazi drop down list,
				// kde sa daju potom produkty vybrat, lebo program to nedokaze

			}
		}

		// return sorted by best offer store (any or to je jedno su posledne) -> any nemam - to je null takze null su posledne
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

	private boolean isProductComparable(Map<StoreName, List<Product>> productsByStore) {
		Collection<List<Product>> foundProducts = productsByStore.values();
		// if we found only one store with only one match for searched products
		if (foundProducts.size() == 1 && foundProducts.iterator().next().size() == 1) {
			return true;
		}
		int offersWithOneProduct = 0;
		for (List<Product> products : foundProducts) {
			if (products.size() == 1) {
				offersWithOneProduct++;
			}
		}
		// if we only one product in each store, than there is possibility to pick the best offer
		// if there were multiple products found in one store for the searched value and others
		// have only one product...there is indication that the client request could be more specific
		// and so we are not able to pick the correct result
		return offersWithOneProduct == foundProducts.size();
	}

	// pomocna metoda, co mi premapuje kluce tak, ze vytiahne prvy prvok z listu - mozne pouzit ked prejde kontrolou na moznost porovnania
	private Map<StoreName, Product> getAsSingleProductMap(Map<StoreName, List<Product>> map) {
		return map.entrySet()
			.stream()
			.collect(toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
	}

	// metoda, co mi da obchod a produkt podla toho, kde sa nachadza
	private Map<StoreName, Product> getBestStoreOffer(Map<StoreName, Product> foundProduct) {
		// - tu je moznost bud najnizsia cena alebo najvacsia zlava
		Map.Entry<StoreName, Product> bestOffer = Collections.min(foundProduct.entrySet(), Comparator.comparingDouble(e -> e.getValue().getPrice()));

		Map<StoreName, Product> multiplePotentialbestOffers = foundProduct.entrySet()
			.stream()
			.filter(e -> e.getKey() != bestOffer.getKey())
			.filter(e -> e.getValue().getPrice() == bestOffer.getValue().getPrice())
			.collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

		multiplePotentialbestOffers.put(bestOffer.getKey(), bestOffer.getValue());

		return multiplePotentialbestOffers;
	}

	private List<Map<String, Object>> toProductListWithStoreElement(Map<StoreName, Product> bestOffer) {
		return bestOffer.entrySet()
			.stream()
			.map(this::appendStoreName)
			.collect(toList());
	}

	private Map<String, Object> appendStoreName(Map.Entry<StoreName, Product> entry) {
		Map<String, Object> productMap = entry.getValue().toMap();
		productMap.put("best_offer_store", entry.getKey());
		return productMap;
	}
}
