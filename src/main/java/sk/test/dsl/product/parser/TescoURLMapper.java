package sk.test.dsl.product.parser;

import static sk.test.dsl.product.Category.CERSTVE_VYROBKY;
import static sk.test.dsl.product.Category.MASO_UDENINY;
import static sk.test.dsl.product.Category.MRAZENE_VYROBKY;
import static sk.test.dsl.product.Category.NAPOJE;
import static sk.test.dsl.product.Category.OVOCIE_ZELENINA;
import static sk.test.dsl.product.Category.TRVANLIVE_POTRAVINY;

import java.util.EnumMap;

import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.CategoryURLMapper;

@Component
public class TescoURLMapper implements CategoryURLMapper {

	private static final String BASE_URL = "https://tesco.sk/akciove-ponuky/akciove-produkty/";
	private static final EnumMap<Category, String> CATEGORY_ENDPOINT_MAP;

	// TODO: review mappings since categories intersect
	static {
		CATEGORY_ENDPOINT_MAP = new EnumMap<>(Category.class);
		CATEGORY_ENDPOINT_MAP.put(MASO_UDENINY, appendToBaseUrl("maso-ryby-a-udeniny/"));
		CATEGORY_ENDPOINT_MAP.put(NAPOJE, appendToBaseUrl("napoje/"));
		CATEGORY_ENDPOINT_MAP.put(CERSTVE_VYROBKY, appendToBaseUrl("mliecne-vyrobky-vajcia-a-pecivo/"));
		CATEGORY_ENDPOINT_MAP.put(OVOCIE_ZELENINA, appendToBaseUrl("ovocie-a-zelenina/"));
		CATEGORY_ENDPOINT_MAP.put(TRVANLIVE_POTRAVINY, appendToBaseUrl("trvanlive-potraviny/"));
		CATEGORY_ENDPOINT_MAP.put(MRAZENE_VYROBKY, appendToBaseUrl("mrazene-potraviny/"));
	}

	private static String appendToBaseUrl(String uri) {
		return BASE_URL + uri;
	}

	@Override
	public EnumMap<Category, String> getCategoryURLMap() {
		return new EnumMap<>(CATEGORY_ENDPOINT_MAP);
	}

	public String getPagedURLByCategory(Category category, int page) {
		String pageUrl = CATEGORY_ENDPOINT_MAP.get(category);
		if (page > 1) {
			return pageUrl + "?page=" + page;
		}
		return pageUrl;
	}
}
