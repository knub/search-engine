package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
import de.hpi.krestel.mySearchEngine.searching.query.UnaryOperator;

public class Word extends UnaryOperator implements Operator {

    private String word;

	public String getWord() {
		return word;
	}

	public Word(String token) {
        this.word = token;
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this;

        return operator.pushWord(this);
    }

    @Override
    public Operator pushWord(Word operator)
    {
        return new RankedWord(this, operator);
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    OccurrenceMap result =  searcher.search(word);
	    return result;
    }
}
