package de.hpi.krestel.mySearchEngine;

// This file will be used to evaluate your search engine!
// You can use/change this file for development. But
// any changes you make here will be ignored for the final test!

// You can use and are encouraged to use multi-threading, map-reduce, etc for
// indexing and/or searching
// The final evaluation will be done with 2GB RAM (java -Xmx2g)!

public class SearchEngineTest {
	// Some test queries for development. The real test queries will be more difficult ;)
//	static String[] queries = {"Artikel", "deutsch", "Artikel AND deutsch", "Artikel OR deutsch", "deutsch BUT NOT Artikel", "\"Filmfestspiele in Venedig\""};
//	static String[] queries = {"Artikel AND Smithee", "Artikel OR Reaktion", "Art* BUT NOT Artikel", "\"Filmfestspiele in Venedig\""};
//	static String[] queries = {"artikel regisseur", "regisseur", "deutsch", "anschluss", "soziologie"};
//	static String[] queries = {"anschluss luhmann", "actinium", "information retrieval"};
//	static String[] queries = {"deutsch"};
//	static String[] queries = {"regisseur"};
    static String[] queries = {
            "\"ein trauriges Arschloch\"",
            "Toskana AND Wein",
            "sülz* AND staatlich",
            "öffentlicher nahverkehr stadtpiraten",
            "schnitzel AND kaffe BUT NOT schwein",
            "Dr. No",
            "ICE BUT NOT T",
            "Bierzelt Oktoberfest",
            "Los Angeles sport",
            "08/15"
    };

	// some variables (will be explained when needed, ignore for now!)
	static int topK = 10;
	static int prf = 5;

	public static void main(String[] args) {
		evaluateSearchEngine();
	}

	private static void evaluateSearchEngine() {
		// Get a new search engine
		SearchEngine se1 = new SearchEngineLynette();
		evaluate(se1);
	}

	private static void evaluate(SearchEngine se) {
		// Load or generate the index
		se.indexWrapper();

		for (int i = 0; i < SearchEngineTest.queries.length; i++) {
			// Search and store results
			se.searchWrapper(queries[i], topK, prf);
		}
	}
}
