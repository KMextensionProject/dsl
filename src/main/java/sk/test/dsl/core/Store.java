package sk.test.dsl.core;

import java.time.DayOfWeek;
import java.util.List;

public abstract class Store {

	public abstract List<DayOfWeek> getDiscountUpdatingDays();

	public abstract List<Product> getDiscountProducts();

}
