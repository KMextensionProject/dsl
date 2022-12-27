package sk.test.dsl.store.utils;

import static sk.test.dsl.core.Category.CERSTVE_VYROBKY;
import static sk.test.dsl.core.Category.KAVA_CAJ_SLADKE_SLANE;
import static sk.test.dsl.core.Category.LAHODKY;
import static sk.test.dsl.core.Category.MASO_UDENINY;
import static sk.test.dsl.core.Category.MRAZENE_VYROBKY;
import static sk.test.dsl.core.Category.NAPOJE;
import static sk.test.dsl.core.Category.OSTATNE;
import static sk.test.dsl.core.Category.OVOCIE_ZELENINA;
import static sk.test.dsl.core.Category.PECIVO;
import static sk.test.dsl.core.Category.TRVANLIVE_POTRAVINY;

import java.util.EnumMap;

import sk.test.dsl.core.Category;
import sk.test.dsl.core.CategoryURLMapper;

public class KauflandURLMapper implements CategoryURLMapper {

	private static final String BASE_URL = "https://www.kaufland.sk/aktualna-ponuka/aktualny-tyzden/akciove-vyrobky.category=";
	private static final EnumMap<Category, String> CATEGORY_ENDPOINT_MAP;

	static {
		CATEGORY_ENDPOINT_MAP = new EnumMap<>(Category.class);
		CATEGORY_ENDPOINT_MAP.put(MASO_UDENINY, appendToBaseUrl("01_M%C3%A4so__hydina__%C3%BAdeniny.html"));
		CATEGORY_ENDPOINT_MAP.put(NAPOJE, appendToBaseUrl("08_N%C3%A1poje.html"));
		CATEGORY_ENDPOINT_MAP.put(CERSTVE_VYROBKY, appendToBaseUrl("03_%C4%8Cerstv%C3%A9_v%C3%BDrobky.html"));
		CATEGORY_ENDPOINT_MAP.put(OVOCIE_ZELENINA, appendToBaseUrl("01a_%C4%8Cerstv%C3%A9_ovocie_a_zelenina__Kvety.html"));
		CATEGORY_ENDPOINT_MAP.put(KAVA_CAJ_SLADKE_SLANE, appendToBaseUrl("07_K%C3%A1va__%C4%8Daj__sladk%C3%A9__slan%C3%A9.html"));
		CATEGORY_ENDPOINT_MAP.put(PECIVO, appendToBaseUrl("06a_Pe%C4%8Divo.html"));
		CATEGORY_ENDPOINT_MAP.put(TRVANLIVE_POTRAVINY, appendToBaseUrl("06_Trvanliv%C3%A9_potraviny.html"));
		CATEGORY_ENDPOINT_MAP.put(LAHODKY, appendToBaseUrl("05_Lah%C3%B4dky.html"));
		CATEGORY_ENDPOINT_MAP.put(MRAZENE_VYROBKY, appendToBaseUrl("04_Mrazen%C3%A9_v%C3%BDrobky.html"));
		CATEGORY_ENDPOINT_MAP.put(OSTATNE, appendToBaseUrl("default_Ostatné.html")); // ?
	}

	private static String appendToBaseUrl(String uri) {
		return BASE_URL + uri;
	}

	@Override
	public EnumMap<Category, String> getCategoryURLMap() {
		return new EnumMap<>(CATEGORY_ENDPOINT_MAP);
	}
}
