package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;

public interface Operator {

    public OccurrenceMap evaluate(IndexSearcher searcher);

}
