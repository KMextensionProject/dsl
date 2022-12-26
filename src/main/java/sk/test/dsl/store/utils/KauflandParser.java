package sk.test.dsl.store.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import sk.test.dsl.core.Category;
import sk.test.dsl.core.HTMLProductParser;
import sk.test.dsl.core.Product;

public class KauflandParser implements HTMLProductParser {

	private static final DecimalFormat PERCENTAGE_DISCOUNT_FORMAT = new DecimalFormat("0.#");

	@Override
	public List<Product> parseHtmlProductsInfo(Document htmlPage, Category productsCategory) {
		Elements tiles = htmlPage.select(".g-col.o-overview-list__list-item");
		List<Product> products = new ArrayList<>();

		for (Element tile : tiles) {
			Elements descriptionPart = tile.select(".m-offer-tile__text");

			// the whole tile may be empty / used as advertisement
			if (descriptionPart.isEmpty()) {
				continue;
			}

			String h5 = descriptionPart.select(".m-offer-tile__subtitle").text();
			String h4 = descriptionPart.select(".m-offer-tile__title").text();
			String quantity = descriptionPart.select(".m-offer-tile__quantity").text().replace(" ", "");

			Elements discountPartParent = tile
				.select(".m-offer-tile__split")
				.select(".m-offer-tile__price-tiles")
				// this section is parent to discount products only and not to those marked "iba"
				.select(".a-pricetag.a-pricetag--discount");

			// there may be a non-discount product -> "iba"
			if (discountPartParent.isEmpty()) {
				continue;
			}

			String discountPercentage = discountPartParent.select(".a-pricetag__discount").text();
			Elements discountPartChild = discountPartParent.select(".a-pricetag__price-container ");

			String price = discountPartChild.select(".a-pricetag__price").text();
			String prevPrice = discountPartChild
				.select(".a-pricetag__old-price")
				.select(".a-pricetag__old-price.a-pricetag__line-through")
				.text();

			Product product = new Product();
			product.setName(resolveProductName(h5, h4));
			product.setCategory(productsCategory);
			product.setQuantityWithUnit(quantity);
			setClubCardBondIfPresent(price, product);
			product.setPrice(parsePrice(price));
			product.setPreviousPrice(parsePrice(prevPrice));
			product.setPercentageDiscount(parseDiscountPercentage(discountPercentage)); // ERROR: kava, caj, sladke, slane "1/2CENY!"

			products.add(product);
		}

		return products;
	}

	private String resolveProductName(String title, String subtitle) {
		if (!subtitle.isEmpty()) {
			return title + " " + subtitle;
		}
		return title;
	}

	private void setClubCardBondIfPresent(String textPrice, Product product) {
		if (textPrice.indexOf(' ') >= 0) {
			product.setClubCardBounded();
		}
	}

	private String parseDiscountPercentage(String discountPercentage) {
		// sometimes they use to put "1/2 CENA!" instead of number
		if (discountPercentage.startsWith("1/2")) {
			return "-50%";
		}

		String discountPercentageCopy = discountPercentage
			.replace(" ", "")  // -29%-30% 
			.replace('%', ' ') // -29 -30
			.trim();
		return PERCENTAGE_DISCOUNT_FORMAT.format(parsePrice(discountPercentageCopy)) + "%"; // -30%
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
}
