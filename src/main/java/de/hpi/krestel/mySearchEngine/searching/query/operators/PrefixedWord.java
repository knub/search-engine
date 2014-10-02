package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.*;

public class PrefixedWord extends UnaryOperator implements Operator {

    private String prefix;

    public PrefixedWord(String token)
    {
        this.prefix = token;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this;

        return operator.pushPrefixedWord(this);
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher)
    {
        return searcher.searchPrefixed(this.prefix);
    }
}
