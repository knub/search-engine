package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;

import java.util.Arrays;

public class Phrase implements Operator {

    private String[] phrase;

    public Phrase(String[] tokens) {
        this.phrase = tokens;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    for (String word : phrase) {
//		    System.out.println("PhraseWord: " + word);
		    OccurrenceMap result = searcher.search(word);
//		    System.out.println(result);
	    }
	    return null;
    }
}
