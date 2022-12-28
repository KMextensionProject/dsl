package sk.test.dsl.store;

import static java.time.DayOfWeek.WEDNESDAY;

import java.time.DayOfWeek;
import java.util.List;

import sk.test.dsl.product.Product;
import sk.test.dsl.store.parser.KauflandParser;
import sk.test.dsl.store.parser.KauflandURLMapper;

// list of products for this store should be updated / cached
// who will update it, or where will it be actually stored?
// 
public class Kaufland extends Store {

	// add spring IoC to manage these
	private KauflandURLMapper urlMapper;
	private KauflandParser parser;

	@Override
	public List<Product> getDiscountProducts() {
		// returne cached values?
		return null;
	}

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return List.of(WEDNESDAY);
	}

	// implement Update method which will be called by the cron
	// and so the Store parent class should have pass the contract to this class for it?
	
}
