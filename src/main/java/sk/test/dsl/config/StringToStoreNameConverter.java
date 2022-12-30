package sk.test.dsl.config;

import org.springframework.core.convert.converter.Converter;

import sk.test.dsl.store.StoreName;

public class StringToStoreNameConverter implements Converter<String, StoreName> {

	/**
	 * May return null.
	 */
	@Override
	public StoreName convert(String source) {
		try {
			return StoreName.valueOf(source.toUpperCase());
		} catch (IllegalArgumentException iaex) {
			return null;
		}
	}
}
