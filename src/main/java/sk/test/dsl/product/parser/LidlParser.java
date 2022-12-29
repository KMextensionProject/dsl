package sk.test.dsl.product.parser;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.HTMLProductParser;
import sk.test.dsl.product.Product;

@Component("lidlParser")
public class LidlParser implements HTMLProductParser {

	@Override
	public List<Product> parseHtmlProductsInfo(Document htmlPage, Category productsCategory) throws IOException {
		List<Product> products = new ArrayList<>(50);	
		List<String> productUrls = parseProductCanonicalUrls(htmlPage);
		for (String url : productUrls) {
			Document htmlProduct = fetchProductPageContent(url);
			Optional<Product> product = parseHtmlProduct(htmlProduct, productsCategory);
			if (!product.isPresent()) {
				continue;
			}
			products.add(product.get());
		}
		return products;
	}

	private List<String> parseProductCanonicalUrls(Document htmlPage) {
		String basePageUrl = "https://www.lidl.sk";
		return htmlPage
			.selectFirst(".ACampaignGrid")
			.select(".ACampaignGrid__item.ACampaignGrid__item--product")
			.stream()
			.map(p -> basePageUrl + p.getElementsByAttribute("canonicalUrl").first().attr("canonicalUrl"))
			.collect(toList());
	}

	private Document fetchProductPageContent(String url) throws IOException {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line);
			}
		}
		return Jsoup.parse(content.toString());
	}

	private Optional<Product> parseHtmlProduct(Document htmlPage, Category category) {
		Element textInfoContainer = htmlPage.selectFirst(".detail__column.detail__column--keyfacts");
		Element priceInfoContainer = htmlPage.selectFirst(".detail__column.detail__column--buybox");
		Element previousPriceHolder = priceInfoContainer.selectFirst(".m-price__top");

		// this can happen when there is no previous price but price label instead mentioning
		// "Supercena" or it has the discount percentage but unverifiable against the missing
		// previous price
		if (previousPriceHolder == null) {
			return Optional.empty();
		}

		String previousPrice = previousPriceHolder.text();
		String name = textInfoContainer.selectFirst(".keyfacts__title").text();
		String quantityWithUnit = textInfoContainer.select(".keyfacts__supplemental-description").text();
		String percentageDiscount = priceInfoContainer.select(".m-price__label").text();
		String currentPrice = priceInfoContainer.selectFirst(".m-price__bottom").text();

		return Optional.of(new Product.ProductBuilder()
			.withName(name)
			.withMeasurementUnit(quantityWithUnit)
			.withPercentageDiscount(percentageDiscount)
			.withPreviousPrice(Double.parseDouble(previousPrice))
			.withCurrentPrice(Double.parseDouble(currentPrice))
			.withCategory(category)
			.createProduct());
	}
}
