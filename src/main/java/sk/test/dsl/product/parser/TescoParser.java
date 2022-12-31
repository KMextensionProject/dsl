package sk.test.dsl.product.parser;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import sk.test.dsl.product.Category;
import sk.test.dsl.product.HTMLProductParser;
import sk.test.dsl.product.Product;

@Component("tescoParser")
public class TescoParser implements HTMLProductParser {

	// TODO: replace by calling url that returns json which was not noticed until now..this is the url:
	// https://tesco.sk/Ajax?apage=1&limit=500&type=load-more-products&path=/akciove-ponuky/akciove-produkty/&get={}&page_url=/akciove-ponuky/akciove-produkty

	@Override
	public List<Product> parseHtmlProductsInfo(Document htmlPage, Category productsCategory) {
		List<Product> products = new ArrayList<>(100);

		removeDuplicateTiles(htmlPage);

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
				.withBondToClubCard(true) // did not find the other case
				.createProduct();

			products.add(product);
		}

		return products;
	}

	private void removeDuplicateTiles(Document htmlPage) {
		/*
		 * all tesco pages have the tiles from previous pages loaded into hidden elements,
		 * this also happens when there is only one page, but hidden elements contain all
		 * the visible tiles as duplicates
		 */
		Elements hiddenTiles = htmlPage
			.selectFirst(".product-container.m-productListing__productsGrid.mobile.hidden.visible-xx-fixed.visible-xs-fixed.visible-sm-fixed")
			.select(".a-productListing__productsGrid__element");

		Elements visibleTiles = htmlPage
			.selectFirst(".product-container.m-productListing__productsGrid.desktop.hidden.visible-md")
			.select(".a-productListing__productsGrid__element");

		/*
		 * when paged, we need to delete all the visible tiles which appear on the current page
		 * because hidden elements contain them all, otherwise the hidden elements must be removed
		 * due to possible inconsistency with visible ones
		 */
		if (hiddenTiles.size() >= visibleTiles.size()) {
			visibleTiles.remove();
		} else {
			hiddenTiles.remove();
		}
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
