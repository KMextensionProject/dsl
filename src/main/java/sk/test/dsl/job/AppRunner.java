package sk.test.dsl.job;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.Product;
import sk.test.dsl.product.parser.LidlParser;
import sk.test.dsl.product.parser.LidlURLMapper;

public class AppRunner {

	public static void main(String[] args) throws IOException {
		
		// Kaufland
//		EnumMap<Category, String> kauflandEndpoints = new KauflandURLMapper().getCategoryURLMap();	
//		KauflandParser kauflandParser = new KauflandParser();
//		List<Product> products = new ArrayList<>(250);
//
//		Document htmlPage;
//		for (Map.Entry<Category, String> entry : kauflandEndpoints.entrySet()) {
//			htmlPage = Jsoup.connect(entry.getValue()).get();
//			products.addAll(kauflandParser.parseHtmlProductsInfo(htmlPage, entry.getKey()));
//		}
//
//		Map<Category, List<Product>> productsByCategory = products.stream()
//				.collect(Collectors.groupingBy(Product::getCategory, Collectors.toList()));
//
//		for (Map.Entry<Category, List<Product>> entry : productsByCategory.entrySet()) {
//			writer.append("----------" + entry.getKey() + "----------");
//			writer.append(System.lineSeparator());
//			entry.getValue().forEach(AppRunner::println);
//		}
//
//		// Tesco
//		TescoURLMapper tescoUrlMapper = new TescoURLMapper();
//		TescoParser tescoParser = new TescoParser();
//
//		// ziskam si zakladnu URL 1. stranky pre danu kategoriu
//		String baseCategoryUrl = tescoUrlMapper.getCategoryURLMap().get(Category.TRVANLIVE_POTRAVINY);
//
//		// ziskam si z nej pocet stranok, na kolkatich sa rozpinaju produkty tejto kategorie
//		int pages = tescoParser.getNumberOfAvailablePages(Jsoup.connect(baseCategoryUrl).get());
//
//		// ziskam si podla poslednej strany vsetky produkty (od 1. strany) ako html dokument
//		Document htmlPage = Jsoup.connect(tescoUrlMapper.getPagedURLByCategory(Category.TRVANLIVE_POTRAVINY, pages)).get();
//
//		// extrahujem z html stranky vsetky data o relevantnych produktoch a spravim si z nich java Produkty v konkretnej kategorii
//		List<Product> products = tescoParser.parseHtmlProductsInfo(htmlPage, Category.TRVANLIVE_POTRAVINY);
//
//		// zobrazim si cely zoznam produktov
//		products.forEach(AppRunner::println);
		
		
		// Lidl
		LidlURLMapper mapper = new LidlURLMapper();
		LidlParser parser = new LidlParser();

		List<Product> products = new ArrayList<>(150);
		for (Map.Entry<Category, String> entry : mapper.getCategoryURLMap().entrySet()) {
			List<Product> categoryProducts = parser.parseHtmlProductsInfo(Jsoup.connect(entry.getValue()).get(), entry.getKey());
			products.addAll(categoryProducts);
		}
		
		products.forEach(AppRunner::println);
		println(products.size());
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
