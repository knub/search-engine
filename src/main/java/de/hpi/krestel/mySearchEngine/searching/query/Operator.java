package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;

public interface Operator {

    public ResultSet evaluate(IndexSearcher searcher);

}
