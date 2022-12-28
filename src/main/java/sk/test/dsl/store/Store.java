package sk.test.dsl.store;

import java.time.DayOfWeek;
import java.util.List;

import sk.test.dsl.product.Product;

public abstract class Store {

	public abstract List<DayOfWeek> getDiscountUpdatingDays();

	public abstract List<Product> getDiscountProducts();

}
