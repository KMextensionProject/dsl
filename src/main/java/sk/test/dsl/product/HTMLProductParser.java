package sk.test.dsl.product;

import java.util.List;

import org.jsoup.nodes.Document;

public interface HTMLProductParser {

	public List<Product> parseHtmlProductsInfo(Document htmlPage, Category productsCategory);

}
