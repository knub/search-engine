package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.*;

public class PrefixedWord extends UnaryOperator implements Operator {

    private String word;

    public PrefixedWord(String token) {
        this.word = token;
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this;

        return operator.pushPrefixedWord(this);
    }

    @Override
    public Operator pushWord(Word operator)
    {
        And and = new And();
        and.setLeft(this);
        and.setRight(operator);
        return and;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
        return searcher.searchPrefixed(word);
    }
}
