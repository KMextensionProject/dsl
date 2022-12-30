package sk.test.dsl.config;

import org.springframework.core.convert.converter.Converter;

import sk.test.dsl.product.Category;

public class StringToCategoryConverter implements Converter<String, Category> {

	/**
	 * May return null.
	 */
	@Override
	public Category convert(String source) {
		try {
			return Category.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException iaex) {
			return null;
		}
	}
}
