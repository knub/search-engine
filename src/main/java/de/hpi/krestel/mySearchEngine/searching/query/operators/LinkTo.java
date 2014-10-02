package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
import de.hpi.krestel.mySearchEngine.searching.query.UnaryOperator;

public class LinkTo extends UnaryOperator implements Operator
{
    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this;

        return operator.pushLinkTo(this);
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) throws RuntimeException
    {
        // TODO: @Corni: do it!
        return null;
    }
}
