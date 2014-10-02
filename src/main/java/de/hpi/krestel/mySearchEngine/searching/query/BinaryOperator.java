package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.searching.query.operators.*;

abstract public class BinaryOperator extends AbstractOperator implements Operator
{
    protected Operator left;
    protected Operator right;

    public boolean hasRight()
    {
        return this.right != null;
    }

    public void setLeft(Operator op1)
    {
        this.left = op1;
    }

    public Operator getLeft()
    {
        return this.left;
    }

    public void setRight(Operator op2)
    {
        this.right = op2;
    }

    public Operator getRight()
    {
        return this.right;
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this.asWordOp();

        return operator.pushBinary(this);
    }

    abstract protected Operator asWordOp();

    @Override
    public Operator pushBinary(BinaryOperator operator)
    {
        if (this.hasRight()) {
            operator.setLeft(this);
            return operator;
        } else {
            return operator.asWordOp().pushOnto(this);
        }
    }

    @Override
    public Operator pushWord(Word operator)
    {
        return this.pushOperator(operator);
    }

    @Override
    public Operator pushRankedWord(RankedWord operator)
    {
        return this.pushOperator(operator);
    }

    @Override
    public Operator pushPrefixedWord(PrefixedWord operator)
    {
        return this.pushOperator(operator);
    }

    @Override
    public Operator pushPhrase(Phrase operator)
    {
        return this.pushOperator(operator);
    }

    @Override
    public Operator pushLinkTo(LinkTo operator)
    {
        return this.pushOperator(operator);
    }

    private Operator pushOperator(Operator operator)
    {
        if (this.hasRight()) {
            this.right = operator.pushOnto(this.right);
        } else {
            this.right = operator;
        }

        return this;
    }
}
