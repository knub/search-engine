package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;

public interface Operator {

    public Operator pushOnto(Operator operator) throws QueryException;

    public Operator pushBinary(BinaryOperator operator);

    public Operator pushWord(Word operator);

    public Operator pushRankedWord(RankedWord operator);

    public Operator pushPrefixedWord(PrefixedWord operator);

    public Operator pushPhrase(Phrase operator);

    public Operator pushLinkTo(LinkTo operator);

    public OccurrenceMap evaluate(IndexSearcher searcher) throws RuntimeException;

}
