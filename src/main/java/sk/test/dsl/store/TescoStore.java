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
import sk.test.dsl.product.parser.TescoParser;
import sk.test.dsl.product.parser.TescoURLMapper;

@Component
public class TescoStore extends Store {

	private static final Logger LOGGER = Logger.getLogger(TescoStore.class.getName());

	@Autowired
	public TescoStore(
		@Qualifier("tescoURLMapper") TescoURLMapper urlMapper, 
		@Qualifier("tescoParser") TescoParser parser) {
		super(urlMapper, parser);
	}

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		// every Wednesday but we've noticed little changes every day since the discount products availability is timed
		return Arrays.asList(DayOfWeek.values());
	}

	@Scheduled(cron = "0 0 8 * * MON-SUN")
	@Override
	public void updateDiscountProductList() throws IOException {
		LOGGER.info("Updating Tesco product list..");
		long start = System.currentTimeMillis();

		List<Product> products = new ArrayList<>(400);
		for (Map.Entry<Category, String> entry : urlMapper.getCategoryURLMap().entrySet()) {
			Category category = entry.getKey();
			String baseCategoryUrl = entry.getValue();
			int pages = ((TescoParser) productParser).getNumberOfAvailablePages(Jsoup.connect(baseCategoryUrl).get());
			String pagedUrl = ((TescoURLMapper) urlMapper).getPagedURLByCategory(category, pages);
			Document pageWithAllProducts = Jsoup.connect(pagedUrl).get();
			List<Product> categoryProducts = productParser.parseHtmlProductsInfo(pageWithAllProducts, category);
			products.addAll(categoryProducts);
		}

		this.discountProducts = Collections.unmodifiableList(products);
		LOGGER.info("Tesco product list has been successfully updated to " + products.size() 
			+ " products in " + (System.currentTimeMillis() - start) + "ms");
	}

	@PostConstruct
	private void initializeStore() {
		try {
			updateDiscountProductList();
		} catch (IOException ioe) {
			LOGGER.severe(() -> "Error initializing Lidl product list, shuting down with: " + ioe.getMessage());
			System.exit(500);
		}
	}
}
