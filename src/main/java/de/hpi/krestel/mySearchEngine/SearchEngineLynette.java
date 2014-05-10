package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.indexing.Indexer;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryParser;
import gnu.trove.iterator.TIntIterator;
import org.javatuples.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private final QueryParser queryParser = new QueryParser(Pipeline.createSearchPipeline());

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
		try {
			File seekFile = new File(directory + "/" + "seek_list");
			if (seekFile.exists()) {
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(seekFile));
				SeekList seekList = (SeekList) ois.readObject();
				searcher.setSeekList(seekList);
				// hard-coding index file for now
				searcher.setIndexFilename(directory + "/final_index0001");
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	ArrayList<String> search(String query, int topK, int prf) {
        Operator op = queryParser.parse(query);
		ResultSet results = op.evaluate(searcher).toResultSet();

        ArrayList<String> resultsStrings = new ArrayList<String>(results.size());


		for (Pair<Integer, Double> result : results) {
			resultsStrings.add("Document: " + result.getValue0() + ", Rank: " + result.getValue1());
		}

        return resultsStrings;
	}



	@Override
	Double computeNdcg(String query, ArrayList<String> ranking, int ndcgAt) {
		// TODO Auto-generated method stub
		return null;
	}
}
