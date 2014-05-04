package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;
import gnu.trove.set.TIntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Phrase implements Operator {

    private String[] phrase;

    public Phrase(String[] tokens) {
        this.phrase = tokens;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    List<OccurrenceMap> singleResults = new ArrayList<OccurrenceMap>();

	    for (String word : phrase) {
		    OccurrenceMap result = searcher.search(word);
		    singleResults.add(result);
		    System.out.println("==========" + word + "=============");
		    System.out.println(result);
	    }
	    int[] commonDocuments = determineCommonDocuments(singleResults);
	    System.out.println("==============Common Documents===============");
	    System.out.println(Arrays.toString(commonDocuments));

	    return null;
    }

	private int[] determineCommonDocuments(List<OccurrenceMap> singleResults) {
		TIntSet commonKeys = singleResults.get(0).keySet();
		Iterator<OccurrenceMap> it = singleResults.iterator();
		// skip first
		it.next();
		while (it.hasNext())
			commonKeys.retainAll(it.next().keySet());
		return commonKeys.toArray();
	}
}
