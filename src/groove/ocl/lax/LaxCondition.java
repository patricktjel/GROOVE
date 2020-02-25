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
    public boolean simplify() {
        boolean toSimplify = false;
        if (expression instanceof EquivVariable) {
            // we have found an equivVariable. The Laxcondition above should start renaming.
            return true;
        }

        // Keep simplifying
        if (condition instanceof LaxCondition) {
            toSimplify = ((LaxCondition) condition).simplify();
        } else if (condition instanceof AndExpression && ((AndExpression) condition).getExpr1() instanceof LaxCondition) {
            toSimplify = ((LaxCondition) ((AndExpression) condition).getExpr1()).simplify();
        }

        // apply equivalence rules
        //TODO: fix implementation
        if (toSimplify) {
            // if an EquivVariable is found we can apply E3
            applyE3();
        }

        // If the condition is null, the expressions are the same and the quantification is the same
        // or the outer quantification is bigger, then they may be merged together
        if (this.condition instanceof LaxCondition && this.expression.equals(((LaxCondition) this.condition).getExpression())
                && this.quantifier.compareTo(((LaxCondition) this.condition).getQuantifier()) >= 0) {
            ((LaxCondition) this.condition).moveConToExpr();
        }
        return false;
    }

    /**
     * Apply Equivalence rule 3
     */
    private void applyE3() {
        EquivVariable equivVar = null;

        if (condition instanceof LaxCondition) {
            equivVar = (EquivVariable) ((LaxCondition) condition).getExpression();

            // remove the equivVar from the condition
            this.condition = null;
        } else if (condition instanceof AndExpression){
            equivVar = (EquivVariable) ((LaxCondition) ((AndExpression) condition).getExpr1()).getExpression();

            // remove the equivVar from the condition
            this.condition = ((AndExpression) condition).getExpr2();
        }

        assert equivVar != null; // If toSimplify is true there has to be an equivVar

        // apply the equivalence declared in equivVar
        this.renameVar(equivVar.getVar2(), equivVar.getVariableName());
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
