package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.BinaryOperator;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;

public class ButNot extends BinaryOperator implements Operator {

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher)  {
        OccurrenceMap result = this.left.evaluate(searcher);
        result.removeResults(this.right.evaluate(searcher));

        return result;
    }
}
