package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.searching.query.operators.Phrase;
import de.hpi.krestel.mySearchEngine.searching.query.operators.PrefixedWord;
import de.hpi.krestel.mySearchEngine.searching.query.operators.RankedWord;
import de.hpi.krestel.mySearchEngine.searching.query.operators.Word;

abstract public class AbstractOperator implements Operator
{
    @Override
    public Operator pushBinary(BinaryOperator operator) {
        throw new RuntimeException(String.format(
                "Cannot push binary operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushWord(Word operator) {
        throw new RuntimeException(String.format(
                "Cannot push word operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushRankedWord(RankedWord operator) {
        throw new RuntimeException(String.format(
                "Cannot push ranked word operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushPrefixedWord(PrefixedWord operator) {
        throw new RuntimeException(String.format(
                "Cannot push prefixed word operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushPhrase(Phrase operator) {
        throw new RuntimeException(String.format(
                "Cannot push phrase operator on %s.",
                this
        ));
    }
}
