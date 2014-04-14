package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.indexing.Indexer;

import java.util.ArrayList;

/* This is your file! Implement your search engine here!
 *
 * Describe your search engine briefly:
 *  - multi-threaded?
 *  - stemming?
 *  - stopword removal?
 *  - index algorithm?
 *  - etc.
 */

public class SearchEngineLynette extends SearchEngine {

	public SearchEngineLynette() {
		// This should stay as is! Don't add anything here!
		super();
	}

	@Override
	void index(String directory) {
		Indexer indexer = new Indexer(directory);
		indexer.run();
	}

	@Override
	boolean loadIndex(String directory) {
		return false;
	}

	@Override
	ArrayList<String> search(String query, int topK, int prf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Double computeNdcg(String query, ArrayList<String> ranking, int ndcgAt) {
		// TODO Auto-generated method stub
		return null;
	}
}
