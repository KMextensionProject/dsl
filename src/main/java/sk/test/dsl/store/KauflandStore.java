package sk.test.dsl.store;

import static java.time.DayOfWeek.WEDNESDAY;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
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
import sk.test.dsl.product.parser.KauflandParser;
import sk.test.dsl.product.parser.KauflandURLMapper;

@Component
public class KauflandStore extends Store {

	@Autowired
	public KauflandStore(
		@Qualifier("kauflandURLMapper") KauflandURLMapper mapper, 
		@Qualifier("kauflandParser") KauflandParser parser) {
		super(mapper, parser);
	}

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return Arrays.asList(WEDNESDAY);
	}

	@Override
	public void updateDiscountProductList() throws IOException {
		List<Product> products = new ArrayList<>(200);
		EnumMap<Category, String> categoryUrls = urlMapper.getCategoryURLMap();
		for (Map.Entry<Category, String> entry : categoryUrls.entrySet()) {
			Document htmlPage = Jsoup.connect(entry.getValue()).get();
			List<Product> categoryProducts = productParser.parseHtmlProductsInfo(htmlPage, entry.getKey());
			products.addAll(categoryProducts);
		}
		this.discountProducts = products;
	}

	@PostConstruct
	private void initializeStore() {
		try {
			System.out.println("initializing kaufland product list...");
			updateDiscountProductList();
			System.out.println("kaufland product list initialized with " + this.discountProducts.size() + " products");
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			System.exit(500);
		}
	}
}
