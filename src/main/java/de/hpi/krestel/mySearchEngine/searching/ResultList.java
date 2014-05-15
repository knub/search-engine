package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;


public class ResultList extends ArrayList<Pair<Integer, DocumentEntry>> {

    public ResultList() {
        super();
    }

    public ResultList(List collection) {
        super(collection);
    }

	@Override
	public ResultList subList(int fromIndex, int toIndex) {
		return new ResultList(super.subList(fromIndex, Math.min(toIndex, this.size())));
	}
}
