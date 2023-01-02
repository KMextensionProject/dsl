package sk.test.dsl.store;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;

import sk.test.dsl.product.CategoryURLMapper;
import sk.test.dsl.product.HTMLProductParser;
import sk.test.dsl.product.Product;

public abstract class Store {

	protected final CategoryURLMapper urlMapper;
	protected final HTMLProductParser productParser;
	protected List<Product> discountProducts;

	public Store(CategoryURLMapper mapper, HTMLProductParser parser) {
		this.urlMapper = mapper;
		this.productParser = parser;
		this.discountProducts = Collections.emptyList();
	}

	public abstract List<DayOfWeek> getDiscountUpdatingDays();

	public abstract void updateDiscountProductList() throws IOException;

	public CategoryURLMapper getUrlMapper() {
		return this.urlMapper;
	}

	public HTMLProductParser getParser() {
		return this.productParser;
	}

	public List<Product> getDiscountProducts() {
		return this.discountProducts;
	}
}
