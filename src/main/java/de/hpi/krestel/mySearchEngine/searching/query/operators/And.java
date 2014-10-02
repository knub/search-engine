package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.BinaryOperator;
import de.hpi.krestel.mySearchEngine.searching.query.OpExecutor;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;

public class And extends BinaryOperator implements Operator
{
    @Override
    protected Operator asWordOp()
    {
        return new Word("and");
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher)
    {
        OpExecutor first = this.runInThread(this.left, searcher);
        OpExecutor second = this.runInThread(this.right, searcher);

        try {
            first.join();
            second.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        OccurrenceMap result = first.getResult();
        this.mergeWithRanks(result, second.getResult());

        return result;
    }
}
