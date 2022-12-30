package sk.test.dsl.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class StringUtils {

	public static String stripDiacritics(String input) {
		return Normalizer.normalize(input, Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	public static boolean containsIgnoreCase(String source, String target) {
		return source.toLowerCase().contains(target.toLowerCase());
	}

}
