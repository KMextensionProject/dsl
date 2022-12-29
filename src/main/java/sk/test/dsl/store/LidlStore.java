package sk.test.dsl.store;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
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
import sk.test.dsl.product.parser.LidlParser;
import sk.test.dsl.product.parser.LidlURLMapper;

@Component
public class LidlStore extends Store {

	@Autowired
	public LidlStore(
		@Qualifier("lidlURLMapper") LidlURLMapper mapper, 
		@Qualifier("lidlParser") LidlParser parser) {
		super(mapper, parser);
	}

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return Collections.emptyList();
	}

	@Override
	public void updateDiscountProductList() throws IOException {
		List<Product> productList = new ArrayList<>(150);
		for (Map.Entry<Category, String> entry : urlMapper.getCategoryURLMap().entrySet()) {
			Category category = entry.getKey();
			Document categoryPage = Jsoup.connect(entry.getValue()).get();
			List<Product> categoryProducts = productParser.parseHtmlProductsInfo(categoryPage, category);
			productList.addAll(categoryProducts);
		}
		this.discountProducts = productList;
	}

	@PostConstruct
	private void initializeStore() {
		try {
			System.out.println("initializing Lidl product list...");
			updateDiscountProductList();
			System.out.println("Lidl product list has been initialized with " + this.discountProducts.size() + " products");
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			System.exit(500);
		}
	}
}
