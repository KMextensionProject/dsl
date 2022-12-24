package sk.test.dsl.core;

public class Product {

	private Category category;
	private String name;
	private String quantityWithUnit;
	private double price; // this can be double - no precision loss
	private double previousPrice;
	private String percentageDiscount;
	private boolean clubCardBounded;

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
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

	public void setQuantityWithUnit(String quantityWithUnit) {
		this.quantityWithUnit = quantityWithUnit;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getPreviousPrice() {
		return previousPrice;
	}

	public void setPreviousPrice(double previousPrice) {
		this.previousPrice = previousPrice;
	}

	public String getPercentageDiscount() {
		return percentageDiscount;
	}

	public void setPercentageDiscount(String percentageDiscount) {
		this.percentageDiscount = percentageDiscount;
	}

	public boolean isClubCardBounded() {
		return clubCardBounded;
	}

	public void setClubCardBounded() {
		this.clubCardBounded = true;
	}

	@Override
	public String toString() {
		return "Product [category=" + category + ", name=" + name + ", quantityWithUnit=" + quantityWithUnit
			+ ", price=" + price + ", previousPrice=" + previousPrice + ", percentageDiscount=" + percentageDiscount
			+ ", clubCardBounded=" + clubCardBounded + "]";
	}
}
