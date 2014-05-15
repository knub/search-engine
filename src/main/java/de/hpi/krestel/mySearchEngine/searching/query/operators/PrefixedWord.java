package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;

public class PrefixedWord implements Operator {

    private String word;

    public PrefixedWord(String token) {
        this.word = token;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    String currentKey = searcher.getSeekList().ceilingKey(word);
	    OccurrenceMap result = new OccurrenceMap();
	    while (currentKey.startsWith(word)) {
		    result.merge(searcher.search(currentKey));
		    currentKey = searcher.getSeekList().higherKey(currentKey);
	    }
	    return result;
    }
}
