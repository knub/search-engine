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
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
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

    String setPlainText = "\033[0;0m";
	String setBoldText = "\033[0;1m";
	//String setPlainText = "";
	//String setBoldText = "";

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
            SeekList seekList = SeekList.createFromFile(directory + "/seek_list");

            if (seekList != null) {
                documents = Documents.readFromFile(directory + "/documents");

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

	@Override
	ArrayList<String> search(String query, int topK, int prf)
    {
        ArrayList<String> resultList;

        this.displayQuery(query);

        try
        {
            Operator op = queryParser.parse(query);

            ResultList results = op.evaluate(searcher) // Execute the search
                    .toResultSet()      // Convert to result set
                    .subList(0, topK);  // Use only the first

            resultList = this.displayResults(results);

//		PseudoRelevanceSearcher prs = new PseudoRelevanceSearcher(results, 300, directory);
//		op = prs.buildNewSearchOperator();
//		results = op.evaluate(searcher).toResultSet().subList(0, topK);
        } catch (QueryException e) {
            resultList = new ArrayList<String>();
        }

        return resultList;
	}

    void displayQuery(String query)
    {
        System.out.println("query: " + query);
    }

    ArrayList<String> displayResults(ResultList results)
    {
        // Add titles to result list
        ArrayList<String> resultsStrings = new ArrayList<String>(results.size());
        SnippetReader snippetReader = new SnippetReader(directory);

        for (Pair<Integer, DocumentEntry> result : results) {
            int docId = result.getValue0();
            DocumentEntry docEntry = result.getValue1();

            String title = this.documents.getTitle(docId);
            resultsStrings.add(title);

            // Display snippets for our results
            System.out.println("\t" + setBoldText + title + setPlainText + " (" + docEntry.getRank() + ")");
            System.out.println("\t\t" + snippetReader.readSnippet(docId, docEntry.offsets.get(0), docEntry.lengths.get(0)));
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

   /* Double computeNdcg(ArrayList<String> goldRanking, ArrayList<String> ranking, int at) {

        double dcg = 0.0;
        double idcg = 0.0;
        int rank=1;
        Iterator<String> iter = ranking.iterator();
        while(rank<=at){
            if(rank==1) idcg += 1+Math.floor(10 * Math.pow(0.5,0.1*rank));
            else idcg += 1+Math.floor(10 * Math.pow(0.5,0.1*rank))/Math.log(rank);
            if(iter.hasNext()){
                // change to get the titles of your ranking
                String title = iter.next().split("###")[1].trim();
                int origRank = goldRanking.indexOf(title)+1;
                if(origRank<1){
                    rank++;
                    continue;
                }
                if(rank==1){
                    dcg += 1+Math.floor(10 * Math.pow(0.5,0.1*origRank));
                    rank++;
                    continue;
                }
                dcg += 1+Math.floor(10 * Math.pow(0.5,0.1*origRank))/Math.log(rank);
            }
            rank++;
        }
        return dcg/idcg;
    }*/

    private int dcgAtRank(int rank) {
        return 1 + (int) Math.floor(10 * Math.pow(0.5, 0.1 * rank));
    }

}
