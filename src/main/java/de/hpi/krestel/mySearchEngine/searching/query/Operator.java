package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.operators.Phrase;
import de.hpi.krestel.mySearchEngine.searching.query.operators.PrefixedWord;
import de.hpi.krestel.mySearchEngine.searching.query.operators.RankedWord;
import de.hpi.krestel.mySearchEngine.searching.query.operators.Word;

public interface Operator {

    public Operator pushOnto(Operator operator) throws RuntimeException;

    public Operator pushBinary(BinaryOperator operator);

    public Operator pushWord(Word operator);

    public Operator pushRankedWord(RankedWord operator);

    public Operator pushPrefixedWord(PrefixedWord operator);

    public Operator pushPhrase(Phrase operator);

    public OccurrenceMap evaluate(IndexSearcher searcher) throws RuntimeException;

}
