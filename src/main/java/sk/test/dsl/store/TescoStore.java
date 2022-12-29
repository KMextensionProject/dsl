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
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.product.parser.TescoParser;
import sk.test.dsl.product.parser.TescoURLMapper;

@Component
public class TescoStore extends Store {

	private List<Product> discountProducts;

	public TescoStore() {
		this.discountProducts = new ArrayList<>(200);
	}

	@Autowired
	private TescoURLMapper mapper;

	@Autowired
	private TescoParser parser;

	@Override
	public List<DayOfWeek> getDiscountUpdatingDays() {
		return Collections.emptyList();
	}

	@Override
	public List<Product> getDiscountProducts() {
		return new ArrayList<>(this.discountProducts);
	}

	@Override
	public void updateDiscountProductList() throws IOException {
		List<Product> products = new ArrayList<>(200);
		for (Map.Entry<Category, String> entry : mapper.getCategoryURLMap().entrySet()) {
			Category category = entry.getKey();
			String baseCategoryUrl = entry.getValue();
			int pages = parser.getNumberOfAvailablePages(Jsoup.connect(baseCategoryUrl).get());
			Document pageWithAllProducts = Jsoup.connect(mapper.getPagedURLByCategory(category, pages)).get();
			List<Product> categoryProducts = parser.parseHtmlProductsInfo(pageWithAllProducts, category);
			products.addAll(categoryProducts);
		}
		this.discountProducts = products;
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
