package sk.test.dsl.store;

import java.time.DayOfWeek;
import java.util.List;

import sk.test.dsl.core.Product;
import sk.test.dsl.core.Store;
import sk.test.dsl.store.utils.KauflandParser;
import sk.test.dsl.store.utils.KauflandURLMapper;

public class Kaufland extends Store {

	// add spring IoC to manage these
	private KauflandURLMapper urlMapper;
	private KauflandParser parser;

	@Override
	public DayOfWeek getDiscountDay() {
		return DayOfWeek.WEDNESDAY;
	}

	@Override
	public List<Product> getDiscountProducts() {
		return null;
	}

}