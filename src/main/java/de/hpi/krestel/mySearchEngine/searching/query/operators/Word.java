package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;

public class Word implements Operator {

    private String word;

    public Word(String token) {
        this.word = token;
    }

    @Override
    public ResultSet evaluate(IndexSearcher searcher) {
        //return result;
	    return searcher.search(word);
    }
}
