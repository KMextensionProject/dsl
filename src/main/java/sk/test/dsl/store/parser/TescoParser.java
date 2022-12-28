package sk.test.dsl.store.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.HTMLProductParser;
import sk.test.dsl.product.Product;

public class TescoParser implements HTMLProductParser {

	@Override
	public List<Product> parseHtmlProductsInfo(Document htmlPage, Category productsCategory) {
		List<Product> products = new ArrayList<>(250);

		// TODO: when passed single page without pagination it automatically fails so it must be capable to select
		// not hidden data this time.

		int hiddenTiles = htmlPage
			.selectFirst(".product-container.m-productListing__productsGrid.mobile.hidden.visible-xx-fixed.visible-xs-fixed.visible-sm-fixed")
			.select(".a-productListing__productsGrid__element")
			.size();

		int visibleTiles = htmlPage
			.selectFirst(".product-container.m-productListing__productsGrid.desktop.hidden.visible-md")
			.select(".a-productListing__productsGrid__element")
			.size();

		if (hiddenTiles >= visibleTiles) { // can they be equal? if so, then we don't care what the source is, but we do not want any duplicates
			// removes the tiled displayed on the current page because when paged, we are getting also hidden tiles
			// contained within previous pages including the current one, so we want to omit the duplicate tiles
			htmlPage.select(".product-container.m-productListing__productsGrid.desktop.hidden.visible-md")
				.select(".a-productListing__productsGrid__element")
				.remove(); // when the top element is not there, this will not do anything
		}

		Elements tiles = htmlPage
			.select(".a-productListing__productsGrid__element")
			.select(".product__info-wrapper");

		for (Element tile : tiles) {
			String productName = tile.selectFirst(".product__name").text();
			Element pricePartParent = tile.selectFirst(".product__secondary-text");
			Elements pricePartChilds = pricePartParent.select(".product__price-cc-text");

			// this means that there is no actual discount
			if (pricePartChilds.size() < 2) {
				continue;
			}

			String currentPrice = pricePartParent.select(".product__price-cc-text").get(1).text();
			String quantityWithUnit = pricePartParent.select("div").get(1).text();
			Element discountPart = tile.selectFirst(".product__discount-percentage");
			String discountPercentage;

			// if there is no percentage discount we need to extract the discount in euro cents
			if (discountPart == null) {
				discountPart = tile.selectFirst(".product__discount");
				discountPercentage = discountPart.select(".saved_price__text").text();
			} else {
				discountPercentage = discountPart.selectFirst(".discount-percentage__text").text().replace(" ", "");
			}

			String previousPrice = discountPart.select(".product__discount-normal-price").text();

			Product product = new Product.ProductBuilder()
				.withName(productName)
				.withCategory(productsCategory)
				.withMeasurementUnit(quantityWithUnit)
				.withPercentageDiscount(discountPercentage)
				.withPreviousPrice(parsePrice(previousPrice))
				.withCurrentPrice(parsePrice(currentPrice))
				.withBondToClubCard(true) // almost everything.. check for different state on web
				.createProduct();

			products.add(product);
		}

		return products;
	}

	private double parsePrice(String textPrice) {
		String price = textPrice.replace(',', '.');
		int delimiterIndex;
		if ((delimiterIndex = price.indexOf(' ')) >= 0) {
			double price1 = Double.parseDouble(price.substring(0, delimiterIndex));
			double price2 = Double.parseDouble(price.substring(delimiterIndex));
			return Math.min(price1, price2);
		}
		return Double.parseDouble(price);
	}

	public int getNumberOfAvailablePages(Document htmlPage) {
		Elements pagination = htmlPage
				.select(".ddl_plp_pagination")
				.select(".label");

		if (pagination == null || pagination.isEmpty()) {
			return 1;
		}
		String lastPage = pagination.get(pagination.size() - 1).text().trim();
		return Integer.parseInt(lastPage);
	}
}
