package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
import de.hpi.krestel.mySearchEngine.searching.query.UnaryOperator;

public class LinkTo extends UnaryOperator implements Operator
{
    private String page;

    public LinkTo(String page)
    {
        this.page = page;
    }

    public String getPage()
    {
        return this.page;
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this;

        return operator.pushLinkTo(this);
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher)
    {
        // find names of the page
        OccurrenceMap result =  searcher.search(page);

        return result;
    }
}
