package sk.test.dsl.main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sk.test.dsl.core.Category;
import sk.test.dsl.core.Product;
import sk.test.dsl.store.utils.KauflandParser;
import sk.test.dsl.store.utils.KauflandURLMapper;

public class AppRunner {

	public static void main(String[] args) throws IOException {
//		Document doc = Jsoup.connect("https://www.kaufland.sk/aktualna-ponuka/aktualny-tyzden/akciove-vyrobky.category=01_M%C3%A4so__hydina__%C3%BAdeniny.html").get();
//		Map<Boolean, List<Product>> productsPartitionedByClubCardBond = new KauflandParser().parseHtmlProductsInfo(doc, Category.MASO_UDENINY).stream()
//			.collect(Collectors.partitioningBy(Product::isClubCardBounded));
//
//		System.out.println("------------ PRODUCTS BOUNDED TO CLUB CARD ------------\n");
//		productsPartitionedByClubCardBond.get(true).forEach(System.out::println);
//		System.out.println("\n--------- PRODUCTS WITH NO BOUND TO CLUB CARD ---------\n");
//		productsPartitionedByClubCardBond.get(false).forEach(System.out::println);

		EnumMap<Category, String> kauflandEndpoints = new KauflandURLMapper().getCategoryURLMap();	
		KauflandParser kauflandParser = new KauflandParser();
		List<Product> products = new ArrayList<>(250);

		Document htmlPage;
		for (Map.Entry<Category, String> entry : kauflandEndpoints.entrySet()) {
			htmlPage = Jsoup.connect(entry.getValue()).get();
			products.addAll(kauflandParser.parseHtmlProductsInfo(htmlPage, entry.getKey()));
		}

		Map<Category, List<Product>> productsByCategory = products.stream()
				.collect(Collectors.groupingBy(Product::getCategory, Collectors.toList()));

		for (Map.Entry<Category, List<Product>> entry : productsByCategory.entrySet()) {
			writer.append("----------" + entry.getKey() + "----------");
			entry.getValue().forEach(AppRunner::println);
		}
	}

	private static BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

	private static void println(Object obj) {
		println(String.valueOf(obj));
	}

	private static void println(CharSequence string) {
		try {
			writer.append(string);
			writer.append(System.lineSeparator());
			writer.flush();
		} catch (IOException ioe) {
			// ignore
		}
	}
}
