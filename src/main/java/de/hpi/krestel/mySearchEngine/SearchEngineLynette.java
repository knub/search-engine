package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.indexing.Indexer;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.Parser;
import gnu.trove.iterator.TIntIterator;

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

    private final IndexSearcher searcher = new IndexSearcher();
    private final Parser queryParser = new Parser(Pipeline.createSearchPipeline());

	public SearchEngineLynette() {
		// This should stay as is! Don't add anything here!
		super();
	}

	@Override
	void index(String directory) {
		Indexer indexer = new Indexer(directory);
		indexer.run();
        searcher.setIndexFilename(indexer.getIndexFilename());
        searcher.setSeekList(indexer.getSeekList());
	}



	@Override
	boolean loadIndex(String directory) {
		return false;
	}

	@Override
	ArrayList<String> search(String query, int topK, int prf) {
        Operator op = null;
        op = queryParser.parse(query);
        ResultSet result = op.evaluate(searcher);

        ArrayList<String> results = new ArrayList<String>(result.size());

        TIntIterator iterator = result.iterator();
        for (int i = result.size(); i > 0; i--) {
            // Todo: find titles here
            results.add("" + iterator.next());
        }

        return results;
	}



	@Override
	Double computeNdcg(String query, ArrayList<String> ranking, int ndcgAt) {
		// TODO Auto-generated method stub
		return null;
	}
}
