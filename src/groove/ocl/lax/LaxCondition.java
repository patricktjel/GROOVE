package groove.ocl.lax;

public class LaxCondition implements Expression{

    private Quantifier quantifier;
    private Expression expression;
    private Expression condition;

    public LaxCondition(Quantifier quantifier, Expression expression, Expression condition) {
        this(quantifier, expression);
        this.condition = condition;
    }

    public LaxCondition(Quantifier quantifier, Expression expression) {
        this.quantifier = quantifier;
        this.expression = expression;
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public Expression getExpression() {
        return expression;
    }

    public Expression getCondition() {
        return condition;
    }

    /**
     * Apply the equivalence rules to make the Lax condition simple
     * @return  The final LaxCondition result
     */
    public LaxCondition simplify() {
        if (condition instanceof LaxCondition) {
            // first use recursion to find the deepest LaxCondition
            ((LaxCondition) condition).simplify();
        }

        // apply Equivalence rules TODO: fix implementation
        if (condition instanceof AndExpression
                    && ((AndExpression) condition).getExpr1() instanceof LaxCondition
                    && ((LaxCondition) ((AndExpression) condition).getExpr1()).getExpression() instanceof EquivVariable) {
            //E3
            EquivVariable equivVar = (EquivVariable) ((LaxCondition) ((AndExpression) condition).getExpr1()).getExpression();

            // apply the equivalence declared in equivVar
            this.renameVar(equivVar.getVar2(), equivVar.getVariableName());

            // remove the equivVar from the condition
            this.condition = ((AndExpression) condition).getExpr2();
        }

        if (this.condition instanceof LaxCondition && this.expression.equals(((LaxCondition) this.condition).getExpression())) {
            //E1
            System.out.println(this.toString());
            ((LaxCondition) this.condition).moveConToExpr();
        }
        return this;
    }

    /**
     * To be able to apply E1, move the condition to the expression
     * and set condition to null
     */
    private void moveConToExpr() {
        this.expression = condition;
        this.condition = null;
    }

    @Override
    public void renameVar(String o, String n) {
        expression.renameVar(o, n);
        if (condition != null) {
            condition.renameVar(o, n);
        }
    }

    @Override
    public String toString() {
        if (condition == null) {
            return String.format("%s(%s)", quantifier, expression);
        } else {
            return String.format("%s(%s, %s)", quantifier, expression, condition);
        }
    }
}
