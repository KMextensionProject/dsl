package sk.test.dsl.store;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import sk.test.dsl.product.parser.LidlParser;
import sk.test.dsl.product.parser.LidlURLMapper;

@Component
public class LidlStore extends Store {

	private static final Logger LOGGER = Logger.getLogger(LidlStore.class.getName());

	@Autowired
	public LidlStore(
		@Qualifier("lidlURLMapper") LidlURLMapper mapper, 
		@Qualifier("lidlParser") LidlParser parser) {
		super(mapper, parser);
	}

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return Arrays.asList(DayOfWeek.MONDAY);
	}

	@Scheduled(cron = "0 0 8 * * MON")
	@Override
	public void updateDiscountProductList() throws IOException {
		// these category URLs must be updated because they are dynamically generated
		((LidlURLMapper) urlMapper).updateCategoryEndpoints();

		LOGGER.info("Updating Lidl product list..");
		long start = System.currentTimeMillis();

		List<Product> productList = new ArrayList<>(150);
		for (Map.Entry<Category, String> entry : urlMapper.getCategoryURLMap().entrySet()) {
			Category category = entry.getKey();
			String categoryUrl = entry.getValue();
			Document categoryPage = Jsoup.connect(categoryUrl).get();
			List<Product> categoryProducts = productParser.parseHtmlProductsInfo(categoryPage, category);
			productList.addAll(categoryProducts);
		}

		this.discountProducts = Collections.unmodifiableList(productList);
		LOGGER.info("Lidl product list has been successfully updated to " + productList.size() 
		+ " products in " + (System.currentTimeMillis() - start) + "ms");
	}

	@PostConstruct
	private void initializeStore() {
		try {
			updateDiscountProductList();
		} catch (IOException ioe) {
			LOGGER.severe(() -> "Error initializing Lidl product list, shuting down with: " + ioe);
			System.exit(500);
		}
	}
}
