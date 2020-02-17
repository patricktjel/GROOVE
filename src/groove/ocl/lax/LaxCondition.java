package groove.ocl.lax;

import groove.ocl.Quantifier;

public class LaxCondition extends Expression{

    private Quantifier quantifier;
    private Expression expression;
    private Expression condition;

    public LaxCondition(Expression expression, Expression condition) {
        this(Quantifier.EXISTS, expression, condition);
    }

    public LaxCondition(Quantifier quantifier, Expression expression, Expression condition) {
        this.quantifier = quantifier;
        this.expression = expression;
        this.condition = condition;
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %s)", quantifier, expression, condition);
    }
}
