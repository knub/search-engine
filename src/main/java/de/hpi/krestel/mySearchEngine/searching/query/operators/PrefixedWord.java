package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;

public class PrefixedWord implements Operator {

    private String word;

    public PrefixedWord(String token) {
        this.word = token;
    }

    @Override
    public ResultSet evaluate(IndexSearcher searcher) {
        // searcher.retrieveOderSO(this.word

        //return result;
        return null;
    }
}
