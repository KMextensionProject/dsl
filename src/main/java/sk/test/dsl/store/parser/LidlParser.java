package sk.test.dsl.store.parser;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.HTMLProductParser;
import sk.test.dsl.product.Product;

public class LidlParser implements HTMLProductParser {

	@Override
	public List<Product> parseHtmlProductsInfo(Document htmlPage, Category productsCategory) {
		return null;
	}

	public static void main(String[] args) throws Exception {
//		Document htmlPage = Jsoup.connect("https://www.lidl.sk/p/bravcova-krkovicka/p5508655").get(); // toto funguje
		Document htmlPage = Jsoup.connect("https://www.lidl.sk/c/cerstve-maso-a-ryby/a10016161?channel=store&tabCode=Current_Sales_Week").get();

		// Mona Lisa
		htmlPage
			.selectFirst(".ACampaignGrid")
			.select(".ACampaignGrid__item.ACampaignGrid__item--product")
				.forEach(e -> System.out.println(e.getElementsByAttribute("canonicalUrl")
					.first()
					.attr("canonicalUrl")));

		/*
		 * https://www.lidl.sk/
		 * +
		 * p/bravcova-krkovicka/p5508655/
		 * p/bravcove-rebra/p180433/
		 * p/bravcovy-bocik/p64653413/
		 * p/morcacie-prsne-rezne/p786531/
		 * p/kurca-cele/p1770/
		 * p/kuracie-kridla/p1526540/
		 * p/bravcove-pliecko/p1658430/
		 * p/hovaedzi-tatarak-premium/p96536520/
		 * p/hovaedzie-rebro-na-polievku/p78653/
		 * p/hovaedzia-nizka-rostenka-roastbeef/p180318/
		 * p/mlete-hovaedzie-maeso-xxl/p465251/
		 * 
		 * Toto co je dole uz nechcem...musim to oddelit
		 * 
		 * p/udene-kare/p115815/
		 * p/kuracie-horne-dolne-stehna/p180328/
		 * p/bravcovy-gulas-xxl/p7506943/
		 * p/hovaedzia-svieckova/p6710068/
		 * p/sushi-tokyo-fukushi/p5554697/
		 * p/rump-steak/p27382/
		 * p/krevety/p39473/
		 * p/bravcove-kare/p36373
		 */
	}
}
