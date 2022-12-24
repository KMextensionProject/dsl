package sk.test.dsl.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sk.test.dsl.core.Category;
import sk.test.dsl.core.KauflandParser;
import sk.test.dsl.core.Product;

public class AppRunner {

	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect("https://www.kaufland.sk/aktualna-ponuka/aktualny-tyzden/akciove-vyrobky.category=01_M%C3%A4so__hydina__%C3%BAdeniny.html").get();
		Map<Boolean, List<Product>> productsPartitionedByClubCardBond = new KauflandParser().parseHtmlProductsInfo(doc, Category.MASO_UDENINY).stream()
			.collect(Collectors.partitioningBy(Product::isClubCardBounded));

		System.out.println("------------ PRODUCTS BOUNDED TO CLUB CARD ------------\n");
		productsPartitionedByClubCardBond.get(true).forEach(System.out::println);
		System.out.println("\n--------- PRODUCTS WITH NO BOUND TO CLUB CARD ---------\n");
		productsPartitionedByClubCardBond.get(false).forEach(System.out::println);
	}
}
