package de.hpi.krestel.mySearchEngine.indexing;

import org.tartarus.snowball.ext.germanStemmer;

public class StemmerWrapper {

	public static String stem(String word) {
		germanStemmer stemmer = new germanStemmer();
		stemmer.setCurrent(word.toLowerCase());
		if (!stemmer.stem())
			throw new RuntimeException("Stemming not successful for " + word);
		return stemmer.getCurrent();
	}
}
