package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.searching.query.operators.*;

abstract public class UnaryOperator extends AbstractOperator implements Operator
{
    @Override
    public Operator pushBinary(BinaryOperator operator)
    {
        operator.setLeft(this);
        return operator;
    }

    @Override
    public Operator pushWord(Word operator)
    {
        return this.or(operator);
    }

    @Override
    public Operator pushRankedWord(RankedWord operator)
    {
        return this.or(operator);
    }

    @Override
    public Operator pushPrefixedWord(PrefixedWord operator)
    {
        return this.or(operator);
    }

    @Override
    public Operator pushPhrase(Phrase operator)
    {
        return this.or(operator);
    }

    @Override
    public Operator pushLinkTo(LinkTo operator)
    {
        return this.or(operator);
    }

    private Operator or(UnaryOperator operator)
    {
        Or or = new Or();
        or.setLeft(this);
        or.setRight(operator);
        return or;
    }
}
