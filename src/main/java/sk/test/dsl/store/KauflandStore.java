package sk.test.dsl.store;

import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.WEDNESDAY;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.product.parser.KauflandParser;
import sk.test.dsl.product.parser.KauflandURLMapper;

@Component
public class KauflandStore extends Store {

	private static final Logger LOGGER = Logger.getLogger(KauflandStore.class.getName());

	@Autowired
	public KauflandStore(
		@Qualifier("kauflandURLMapper") KauflandURLMapper mapper, 
		@Qualifier("kauflandParser") KauflandParser parser) {
		super(mapper, parser);
	}

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return Arrays.asList(WEDNESDAY, THURSDAY); // is Wednesday OK?
	}

	@Scheduled(cron = "0 0 8 * * WED-THU")
	@Override
	public void updateDiscountProductList() throws IOException {
		LOGGER.info("Updating Kaufland product list..");
		long start = System.currentTimeMillis();

		Set<Product> products = new HashSet<>(250);

		// GET by standard categories 
		EnumMap<Category, String> categoryUrls = urlMapper.getCategoryURLMap();
		for (Map.Entry<Category, String> entry : categoryUrls.entrySet()) {
			String categoryUrl = entry.getValue();
			Document htmlPage = Jsoup.connect(categoryUrl).get();
			List<Product> categoryProducts = productParser.parseHtmlProductsInfo(htmlPage, entry.getKey());
			products.addAll(categoryProducts);
		}

		// GET by special categories
		Document pageWithCategoryMenu = Jsoup.connect(categoryUrls.get(Category.OVOCIE_ZELENINA)).get();
		List<String> specialCategoryUrls = ((KauflandParser) productParser).extractSpecialCategoryURLs(pageWithCategoryMenu);
		for (String menuUrl : specialCategoryUrls) {
			menuUrl = appendHostIfMissing(menuUrl);
			Document additionalCategoryPage = Jsoup.connect(menuUrl).get();
			List<Product> additionalCategoryProducts = productParser.parseHtmlProductsInfo(additionalCategoryPage, Category.OSTATNE);
			products.addAll(additionalCategoryProducts);
		}

		this.discountProducts = Collections.unmodifiableList(new ArrayList<>(products));
		LOGGER.info("Kaufland product list has been successfully updated to " + products.size() 
			+ " products in " + (System.currentTimeMillis() - start) + "ms");
	}

	private String appendHostIfMissing(String categoryUrl) {
		if (!categoryUrl.startsWith("http")) {
			return KauflandURLMapper.BASE_URL + categoryUrl;
		}
		return categoryUrl;
	}

	@PostConstruct
	private void initializeStore() {
		try {
			updateDiscountProductList();
		} catch (IOException ex) {
			LOGGER.severe(() -> "Error initializing Lidl product list, shuting down with: " + ex.getMessage());
			System.exit(500);
		}
	}
}
