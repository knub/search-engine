package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import gnu.trove.TIntCollection;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;


public class ResultSet extends ArrayList<Pair<Integer, DocumentEntry>> {

    public ResultSet() {
        super();
    }

    public ResultSet(List collection) {
        super(collection);
    }
}
