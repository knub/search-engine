package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.AbstractOperator;
import de.hpi.krestel.mySearchEngine.searching.query.BinaryOperator;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import gnu.trove.procedure.TIntObjectProcedure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedWord extends AbstractOperator implements Operator {

	List<Word> words = new ArrayList();

	public RankedWord() {}

	public RankedWord(Word word1, Word word2) {
		add(word1);
		add(word2);
	}

	public void add(Word word) {
		this.words.add(word);
	}

    @Override
    public Operator pushOnto(Operator operator) throws RuntimeException
    {
        if (operator == null) return this;

        return operator.pushRankedWord(this);
    }

    @Override
    public Operator pushWord(Word operator)
    {
        this.add(operator);
        return this;
    }

    @Override
	public OccurrenceMap evaluate(IndexSearcher searcher) {
		Map<String, Integer> queryWords = new HashMap<String, Integer>();
		for (Word word : words) {
			Integer count = queryWords.get(word.getWord());
			if (count == null)
				queryWords.put(word.getWord(), 1);
			else
				queryWords.put(word.getWord(), count + 1);
		}

		final OccurrenceMap resultMap = new OccurrenceMap();
		for (Map.Entry<String, Integer> entry : queryWords.entrySet()) {
			OccurrenceMap newMap = searcher.search(entry.getKey(), entry.getValue());
			newMap.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
				@Override
				public boolean execute(int docId, DocumentEntry docEntry) {
					DocumentEntry currentDocEntry = resultMap.get(docId);
					/*
					 * If we want to show the surrounding of the search result, we have to be more
					 * sophisticated here, since this current implementation only stores the first document entry.
					 */
					if (currentDocEntry == null)
						resultMap.put(docId, docEntry);
					else
						currentDocEntry.setRank(currentDocEntry.getRank() + docEntry.getRank());
					return true;
				}
			});
		}
		return resultMap;
	}
}
