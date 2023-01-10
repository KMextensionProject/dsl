package sk.test.dsl.store;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.product.parser.TescoParser;
import sk.test.dsl.product.parser.TescoURLMapper;

@Component
public class TescoStore extends Store {

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

	@Override
	public void updateDiscountProductList() throws IOException {
		List<Product> products = new ArrayList<>(400);
		for (Map.Entry<Category, String> entry : urlMapper.getCategoryURLMap().entrySet()) {
			Category category = entry.getKey();
			String baseCategoryUrl = entry.getValue();
			int pages = ((TescoParser) productParser).getNumberOfAvailablePages(Jsoup.connect(baseCategoryUrl).get());
			Document pageWithAllProducts = Jsoup.connect(((TescoURLMapper) urlMapper).getPagedURLByCategory(category, pages)).get();
			List<Product> categoryProducts = productParser.parseHtmlProductsInfo(pageWithAllProducts, category);
			products.addAll(categoryProducts);
		}
		this.discountProducts = Collections.unmodifiableList(products);
	}

	@PostConstruct
	private void initializeStore() {
		try {
			System.out.println("initializing Tesco product list...");
			updateDiscountProductList();
			System.out.println("tesco product list has been initialized with " + this.discountProducts.size() + " products");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			System.exit(500);
		}
	}
}
