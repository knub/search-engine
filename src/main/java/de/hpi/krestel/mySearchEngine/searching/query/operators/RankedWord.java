package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
import de.hpi.krestel.mySearchEngine.searching.query.UnaryOperator;
import gnu.trove.procedure.TIntObjectProcedure;

import java.util.HashMap;
import java.util.Map;

public class RankedWord extends UnaryOperator implements Operator {

    Map<String, Integer> words = new HashMap<String, Integer>();

	public RankedWord() {}

	public RankedWord(Word word1, Word word2)
    {
		add(word1);
		add(word2);
	}

	public void add(Word word)
    {
        Integer count = this.words.get(word.getWord());
        if (count == null) {
            this.words.put(word.getWord(), 1);
        } else {
            this.words.put(word.getWord(), count + 1);
        }
	}

    public String[] getWords()
    {
        return this.words.keySet().toArray(new String[this.words.size()]);
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
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
	public OccurrenceMap evaluate(IndexSearcher searcher)
    {
		final OccurrenceMap resultMap = new OccurrenceMap();
		for (Map.Entry<String, Integer> entry : this.words.entrySet()) {
			OccurrenceMap newMap = searcher.search(entry.getKey(), entry.getValue());
            this.mergeWithRanks(resultMap, newMap);
		}
		return resultMap;
	}
}
