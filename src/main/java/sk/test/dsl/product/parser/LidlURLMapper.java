package sk.test.dsl.product.parser;

import static sk.test.dsl.product.Category.MASO_UDENINY;
import static sk.test.dsl.product.Category.OSTATNE;
import static sk.test.dsl.product.Category.OVOCIE_ZELENINA;
import static sk.test.dsl.product.Category.PECIVO;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.CategoryURLMapper;

@Component("lidlURLMapper")
public class LidlURLMapper implements CategoryURLMapper {

	public static final String BASE_URL = "https://www.lidl.sk";
	private final EnumMap<Category, String> categoryEndpointMap;

	@Autowired
	private LidlParser parser;

	public LidlURLMapper() {
		this.categoryEndpointMap = new EnumMap<>(Category.class);
	}

	@Override
	public EnumMap<Category, String> getCategoryURLMap() {
		return new EnumMap<>(categoryEndpointMap);
	}

	public void updateCategoryEndpoints() throws IOException {
		List<String> categoryUrls = parser.extractDynamicCategoryUrls();
		for (String categoryUrl : categoryUrls) {
			addCategoryURLEntry(categoryUrl);
			if (categoryEndpointMap.size() == 4) {
				break;
			}
		}
	}

	private void addCategoryURLEntry(String categoryUrl) {
		if (categoryUrl.contains("maso")) {
			categoryEndpointMap.put(MASO_UDENINY, appendHostIfMissing(categoryUrl));
		} else if (categoryUrl.contains("ovocie")) {
			categoryEndpointMap.put(OVOCIE_ZELENINA, appendHostIfMissing(categoryUrl));
		} else if (categoryUrl.contains("priamo")) {
			categoryEndpointMap.put(PECIVO, appendHostIfMissing(categoryUrl));
		} else if (categoryUrl.contains("hity")) {
			categoryEndpointMap.put(OSTATNE, appendHostIfMissing(categoryUrl));
		}
	}

	private String appendHostIfMissing(String categoryUrl) {
		if (!categoryUrl.startsWith("http")) {
			return BASE_URL + categoryUrl;
		}
		return categoryUrl;
	}
}
