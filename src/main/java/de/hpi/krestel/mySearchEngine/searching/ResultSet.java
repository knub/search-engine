package de.hpi.krestel.mySearchEngine.searching;

import gnu.trove.TIntCollection;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;

public class ResultSet extends TIntArrayList {

    public ResultSet() {
        super();
    }

    public ResultSet(TIntCollection collection) {
        super(collection);
    }
}
