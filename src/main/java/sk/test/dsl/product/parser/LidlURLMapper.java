package sk.test.dsl.product.parser;

import static sk.test.dsl.product.Category.MASO_UDENINY;
import static sk.test.dsl.product.Category.OSTATNE;
import static sk.test.dsl.product.Category.OVOCIE_ZELENINA;
import static sk.test.dsl.product.Category.PECIVO;

import java.util.EnumMap;

import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.CategoryURLMapper;

@Component
public class LidlURLMapper implements CategoryURLMapper {

	private static final String BASE_URL = "https://www.lidl.sk/c/";
	private static final EnumMap<Category, String> CATEGORY_ENDPOINT_MAP;

	static {
		CATEGORY_ENDPOINT_MAP = new EnumMap<>(Category.class);
		CATEGORY_ENDPOINT_MAP.put(MASO_UDENINY, appendToBaseUrl("cerstve-maso-a-ryby/a10016161?channel=store&tabCode=Current_Sales_Week"));
		CATEGORY_ENDPOINT_MAP.put(OVOCIE_ZELENINA, appendToBaseUrl("ovocie-a-zelenina/a10016163?channel=store&tabCode=Current_Sales_Week"));
		CATEGORY_ENDPOINT_MAP.put(PECIVO, appendToBaseUrl("priamo-z-pece/a10016162?channel=store&tabCode=Current_Sales_Week"));
		CATEGORY_ENDPOINT_MAP.put(OSTATNE, appendToBaseUrl("cenove-hity/a10015981?channel=store&tabCode=Current_Sales_Week"));
	}

	private static String appendToBaseUrl(String uri) {
		return BASE_URL + uri;
	}

	@Override
	public EnumMap<Category, String> getCategoryURLMap() {
		return new EnumMap<>(CATEGORY_ENDPOINT_MAP);
	}

}
