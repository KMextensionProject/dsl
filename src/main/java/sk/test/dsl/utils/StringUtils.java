package sk.test.dsl.utils;

import java.text.Normalizer;
import java.text.Normalizer.Form;

public class StringUtils {

	public static String stripDiacritics(String input) {
		return Normalizer.normalize(input, Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean containsPhrase(String source, String phrase) {
		if (source.length() < phrase.length()) {
			return false;
		}
		String sourceText = source.toLowerCase();
		String lookupText = phrase.toLowerCase();
		if (phrase.indexOf(' ') < 0) {
			return sourceText.contains(lookupText);
		}
		String[] lookupWords = phrase.split(" ");
		int matchesFound = 0;
		for (String lookupWord : lookupWords) {
			if (sourceText.contains(lookupWord)) {
				matchesFound++;
			}
		}
		return matchesFound == lookupWords.length;
	}
}
