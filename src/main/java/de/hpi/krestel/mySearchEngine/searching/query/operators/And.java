package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;

public class And implements Operator {

    private Operator left;
    private Operator right;

    public And(Operator op1, Operator op2) {
        this.left = op1;
        this.right = op2;
    }

    @Override
    public ResultSet evaluate(IndexSearcher searcher) {
        ResultSet result = this.left.evaluate(searcher);
        result.retain(this.right.evaluate(searcher));

        return result;
    }
}
