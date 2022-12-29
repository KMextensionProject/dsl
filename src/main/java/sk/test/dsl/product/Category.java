package sk.test.dsl.product;

public enum Category {

	// TODO: add kvety + drogeria?
	MASO_UDENINY,
	NAPOJE,
	CERSTVE_VYROBKY,
	OVOCIE_ZELENINA,
	KAVA_CAJ_SLADKE_SLANE,
	PECIVO,
	TRVANLIVE_POTRAVINY,
	LAHODKY,
	MRAZENE_VYROBKY,
	OSTATNE;

	public static boolean contains(String categoryValue) {
		for (Category category : Category.values()) {
			if (category.name().equalsIgnoreCase(categoryValue)) {
				return true;
			}
		}
		return false;
	}
}