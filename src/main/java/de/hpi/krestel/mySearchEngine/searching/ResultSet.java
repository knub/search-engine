package de.hpi.krestel.mySearchEngine.searching;

import gnu.trove.TIntCollection;
import gnu.trove.set.hash.TIntHashSet;

public class ResultSet extends TIntHashSet {

    public ResultSet() {
        super();
    }

    public ResultSet(TIntCollection collection) {
        super(collection);
    }

    public boolean retain(ResultSet other) {
        return this.retainAll(other);
    }

    public boolean merge(ResultSet other) {
        return this.addAll(other);
    }

    public boolean remove(ResultSet other) {
        return this.removeAll(other);
    }

}
