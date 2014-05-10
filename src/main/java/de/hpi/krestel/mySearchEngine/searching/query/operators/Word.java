package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;

public class Word implements Operator {

    private String word;

	public String getWord() {
		return word;
	}

	public Word(String token) {
        this.word = token;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    OccurrenceMap result =  searcher.search(word);
	    return result;
    }
}
