package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.AbstractOperator;
import de.hpi.krestel.mySearchEngine.searching.query.BinaryOperator;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;

public class Word extends AbstractOperator implements Operator {

    private String word;

	public String getWord() {
		return word;
	}

	public Word(String token) {
        this.word = token;
    }

    @Override
    public Operator pushOnto(Operator operator) throws RuntimeException
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
    public Operator pushBinary(BinaryOperator operator)
    {
        operator.setLeft(this);
        return operator;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    OccurrenceMap result =  searcher.search(word);
	    return result;
    }
}
