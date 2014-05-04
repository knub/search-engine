package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.ResultSet;
import de.hpi.krestel.mySearchEngine.searching.query.PhraseTag;
import gnu.trove.set.TIntSet;

import javax.swing.text.Document;
import java.util.*;

public class Phrase implements Operator {

    private String[] phrase;

    public Phrase(String[] tokens) {
        this.phrase = tokens;
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher) {
	    List<OccurrenceMap> singleResults = searchForEachWord(searcher);
	    int[] commonDocuments = determineCommonDocuments(singleResults);

	    OccurrenceMap resultMap = new OccurrenceMap();
	    for (int docId : commonDocuments) {
		    List<DocumentEntry> docEntries = extractDocumentEntries(singleResults, docId);

		    List<PhraseTag> tags = buildPhraseTags(docEntries);

		    PhraseTag lastBeginningPhrase = null;
		    int lastPosition = Integer.MIN_VALUE;
		    int lastWordIndex = Integer.MIN_VALUE;
		    boolean goodStreak = false;

		    DocumentEntry resultEntry = new DocumentEntry();
		    for (PhraseTag tag : tags) {
			    if (tag.wordIndex == 0) {
				    lastBeginningPhrase = tag;
				    goodStreak = true;
			    }
			    else if (!(lastWordIndex + 1 == tag.wordIndex && lastPosition + 1 == tag.position)) {
				    goodStreak = false;
			    }
			    if (tag.wordIndex == docEntries.size() - 1 && goodStreak) {
				    resultEntry.positions.add(lastBeginningPhrase.position);
				    resultEntry.offsets.add(lastBeginningPhrase.offset);
				    resultEntry.lengths.add(lastBeginningPhrase.length);
				    goodStreak = false;
			    }
			    lastPosition = tag.position;
			    lastWordIndex = tag.wordIndex;
		    }
		    resultMap.put(docId, resultEntry);
	    }

	    return resultMap;
    }

	private List<PhraseTag> buildPhraseTags(List<DocumentEntry> docEntries) {
		List<PhraseTag> tags = new ArrayList<PhraseTag>();
		int docCount = 0;
		for (DocumentEntry docEntry : docEntries) {
			for (int i = 0; i < docEntry.size(); i++)
				tags.add(new PhraseTag(docEntry.positions.get(i), docEntry.offsets.get(i), docEntry.lengths.get(i), docCount));
			docCount++;
		}

		Collections.sort(tags);
		return tags;
	}

	private List<DocumentEntry> extractDocumentEntries(List<OccurrenceMap> singleResults, int docId) {
		List<DocumentEntry> docEntries = new ArrayList<DocumentEntry>();
		for (OccurrenceMap occMap : singleResults)
			docEntries.add(occMap.get(docId));
		return docEntries;
	}

	private List<OccurrenceMap> searchForEachWord(IndexSearcher searcher) {
		List<OccurrenceMap> singleResults = new ArrayList<OccurrenceMap>();

		for (String word : phrase) {
			OccurrenceMap result = searcher.search(word);
			singleResults.add(result);
		}
		return singleResults;
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
