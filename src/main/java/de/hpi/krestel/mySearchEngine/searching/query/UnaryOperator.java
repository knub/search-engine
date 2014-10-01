package de.hpi.krestel.mySearchEngine.searching.query;

abstract public class UnaryOperator extends AbstractOperator implements Operator
{
    @Override
    public Operator pushBinary(BinaryOperator operator)
    {
        operator.setLeft(this);
        return operator;
    }
}
