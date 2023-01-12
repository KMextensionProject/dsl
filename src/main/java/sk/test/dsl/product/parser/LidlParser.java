package sk.test.dsl.product.parser;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.HTMLProductParser;
import sk.test.dsl.product.Product;
import static sk.test.dsl.product.parser.LidlURLMapper.BASE_URL;

@Component("lidlParser")
public class LidlParser implements HTMLProductParser {

	private static final Logger LOGGER = Logger.getLogger(LidlParser.class.getName());

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
		return htmlPage
			.selectFirst(".ACampaignGrid")
			.select(".ACampaignGrid__item.ACampaignGrid__item--product")
			.stream()
			.map(p -> BASE_URL + p.getElementsByAttribute("canonicalUrl").first().attr("canonicalUrl"))
			.collect(toList());
	}

	private Document fetchProductPageContent(String url) throws IOException {
		LOGGER.finer("Calling " + url);
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

	public List<String> extractDynamicCategoryUrls() throws IOException {
		LOGGER.finer("Calling " + BASE_URL + " to extract category URLs");
		Document mainPage = Jsoup.connect(BASE_URL).get();
		String discountPageURL = mainPage.select(".n-header__main-navigation-link").get(1).attr("href");
		discountPageURL += "&channel=store&tabCode=Current_Sales_Week";

		// this is duplicate somewhere else
		if (!discountPageURL.startsWith("http")) {
			discountPageURL = BASE_URL + discountPageURL;
		}

		LOGGER.finer("Calling: " + discountPageURL);
		return Jsoup.connect(discountPageURL).get()
			.select(".ATheHeroStage__SliderTrack.m-ux-slider-track-distances-8").get(1)
			.select(".ATheHeroStage__Offer > a")
			.eachAttr("href");
	}
}
