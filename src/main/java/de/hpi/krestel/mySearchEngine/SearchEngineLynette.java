package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.Documents;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.indexing.Indexer;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.ResultList;
import de.hpi.krestel.mySearchEngine.searching.SnippetReader;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryParser;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
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
    private Documents documents;

    private final String WIKI_FILE = "data/dewiki-20140216-pages-articles-multistream-first-five.xml";
//    private final String WIKI_FILE = "data/dewiki-20140216-pages-articles-multistream.xml";

    //	String setPlainText = "\033[0;0m";
//	String setBoldText = "\033[0;1m";
	String setPlainText = "";
	String setBoldText = "";

	public SearchEngineLynette()
    {
		super();
	}

	@Override
	void index(String directory) {
		Indexer indexer = new Indexer(directory, new WikipediaReader(WIKI_FILE));
		indexer.run();
	}

    /**
     * Load the index if the seek file exists.
     *
     * @param directory
     * @return
     */
	@Override
	boolean loadIndex(String directory) {
		try {
            SeekList seekList = loadSeekListFromFile(directory + "/seek_list");

            if (seekList != null) {
                documents = seekList.getDocuments();

                searcher.setDocuments(documents);
                searcher.setSeekList(seekList);
				searcher.setIndexFilename(directory + "/final_index0001");

                return true;
			}

			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Load the seek list from disk.
     *
     * @param filename
     * @return SeekList
     */
    private SeekList loadSeekListFromFile(String filename) throws Exception {
        File seekFile = new File(filename);

        if (seekFile.exists()) {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(seekFile));
            return (SeekList) ois.readObject();
        }

        return null;
    }

	@Override
	ArrayList<String> search(String query, int topK, int prf) {
        Operator op = queryParser.parse(query);
		ResultList results = op.evaluate(searcher) // Execute the search
                               .toResultSet()      // Convert to result set
                               .subList(0, prf);   // Use only the first

//		PseudoRelevanceSearcher prs = new PseudoRelevanceSearcher(results, 300);
//		op = prs.buildNewSearchOperator();
//		results = op.evaluate(searcher).toResultSet().subList(0, topK);

        // Add titles to result list
        ArrayList<String> resultsStrings = new ArrayList<String>(results.size());
        for (Pair<Integer, DocumentEntry> result : results) {
            int docId = result.getValue0();
            resultsStrings.add(documents.getTitle(docId));
        }

        // Generate snippets for our results
		SnippetReader snippetReader = new SnippetReader();
		for (Pair<Integer, DocumentEntry> result : results) {
			int docId = result.getValue0();
			DocumentEntry docEntry = result.getValue1();
			resultsStrings.add(setBoldText + "    Document: " + documents.getTitle(docId) + ", Rank: " + docEntry.getRank() + setPlainText);
			resultsStrings.add("    " + snippetReader.readSnippet(docId, docEntry.offsets.get(0), docEntry.lengths.get(0)));
		}

        return resultsStrings;
	}

	@Override
	Double computeNdcg(ArrayList<String> goldRanking, ArrayList<String> ranking, int ndcgAt) {
        double goldDcg = 0.0;
        double origDcg = 0.0;

        goldRanking.retainAll(ranking);

        if (ranking != null) {
            int origRank = 1;
            Iterator<String> iter = ranking.iterator();
            while(iter.hasNext() && origRank <= ndcgAt) {
                String item = iter.next();
                if (goldRanking.contains(item)) {
                    int goldRank = goldRanking.indexOf(item) + 1;
                    int goldGain = dcgAtRank(goldRank);
                    int origGain = dcgAtRank(origRank);
                    goldDcg += goldGain;
                    origDcg += origGain;
                }
                origRank++;
            }
        }

        if (goldDcg == 0.0) {
            return 0.0;
        }

        return origDcg / goldDcg;
	}

    private int dcgAtRank(int rank) {
        return 1 + (int) Math.floor(10 * Math.pow(0.5, 0.1 * rank));
    }

}
