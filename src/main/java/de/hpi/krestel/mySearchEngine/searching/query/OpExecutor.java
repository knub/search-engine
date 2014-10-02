package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;

public class OpExecutor extends Thread
{
    private Operator operator;
    private IndexSearcher searcher;
    private OccurrenceMap result;

    public OpExecutor(Operator operator, IndexSearcher searcher)
    {
        this.operator = operator;
        this.searcher = searcher;
    }

    @Override
    public void run()
    {
        this.result = this.operator.evaluate(this.searcher);
    }

    public OccurrenceMap getResult()
    {
        return this.result;
    }
}
