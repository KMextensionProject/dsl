package sk.test.dsl.product;

import java.util.HashMap;
import java.util.Map;

public class Product {

	private Category category;
	private String name;
	private String quantityWithUnit;
	private double price;
	private double previousPrice;
	private String percentageDiscount;
	private boolean clubCardBonded;

	private Product() {
		
	}

	public Category getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuantityWithUnit() {
		return quantityWithUnit;
	}

	public double getPrice() {
		return price;
	}

	public double getPreviousPrice() {
		return previousPrice;
	}

	public String getPercentageDiscount() {
		return percentageDiscount;
	}

	public boolean isClubCardBonded() {
		return clubCardBonded;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> productMap = new HashMap<>();
		productMap.put("category", category);
		productMap.put("name", name);
		productMap.put("quantity_with_unit", quantityWithUnit);
		productMap.put("price", price);
		productMap.put("previous_price", previousPrice);
		productMap.put("percentage_discount", percentageDiscount);
		productMap.put("club_card_bounded", clubCardBonded);
		return productMap;
	}

	@Override
	public String toString() {
		return "Product [category=" + category + ", name=" + name + ", quantityWithUnit=" + quantityWithUnit
			+ ", price=" + price + ", previousPrice=" + previousPrice + ", percentageDiscount=" + percentageDiscount
			+ ", clubCardBounded=" + clubCardBonded + "]";
	}

	public static class ProductBuilder {

		private final Product product = new Product();

		public ProductBuilder withName(String name) {
			product.name = name;
			return this;
		}

		public ProductBuilder withBondToClubCard(boolean isClubCardBonded) {
			product.clubCardBonded = isClubCardBonded;
			return this;
		}

		public ProductBuilder withPercentageDiscount(String percentageDiscount) {
			product.percentageDiscount = percentageDiscount;
			return this;
		}

		public ProductBuilder withPreviousPrice(double previousPrice) {
			product.previousPrice = previousPrice;
			return this;
		}

		public ProductBuilder withCurrentPrice(double price) {
			product.price = price;
			return this;
		}

		public ProductBuilder withMeasurementUnit(String quantityWithUnit) {
			product.quantityWithUnit = quantityWithUnit;
			return this;
		}

		public ProductBuilder withCategory(Category category) {
			product.category = category;
			return this;
		}

		public Product createProduct() {
			return this.product;
		}
	}
}
