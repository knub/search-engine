package de.hpi.krestel.mySearchEngine.searching.query.operators;

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
    public ResultSet evaluate(IndexSearcher searcher) {
	    for (String word : phrase) {
		    System.out.println("PhraseWord: " + word);
	    }
	    return null;
    }
}
