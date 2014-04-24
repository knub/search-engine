package de.hpi.krestel.mySearchEngine;

// This file will be used to evaluate your search engine!
// You can use/change this file for development. But
// any changes you make here will be ignored for the final test!

// You can use and are encouraged to use multi-threading, map-reduce, etc for
// indexing and/or searching
// The final evaluation will be done with 2GB RAM (java -Xmx2g)!

import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;

public class SearchEngineTest {
	// Some test queries for development. The real test queries will be more difficult ;)
	static String[] queries = {"artikel", "deutsch"};

	// some variables (will be explained when needed, ignore for now!)
	static int topK = 10;
	static int prf = 5;

	public static void main(String[] args) {
//		readWikipediaFile();
		evaluateSearchEngine();

		// how to determine free memory
//		System.out.println(Runtime.getRuntime().totalMemory() / 1024 / 1024);
//		System.out.println(Runtime.getRuntime().freeMemory() / 1024 / 1024);
//		System.out.println(Runtime.getRuntime().maxMemory() / 1024 / 1024);
	}

	private static void readWikipediaFile() {
        WikipediaReader wikipediaReader = new WikipediaReader();
		wikipediaReader.readWikiFile();
	}

	private static void evaluateSearchEngine() {
		// Get a new search engine
		SearchEngine se1 = new SearchEngineLynette();
		evaluate(se1);
	}

	private static void evaluate(SearchEngine se) {
		// Load or generate the index
		se.indexWrapper();

//		for (int i = 0; i < SearchEngineTest.queries.length; i++) {
//			// Search and store results
//			se.searchWrapper(queries[i], topK, prf);
//		}
	}
}
