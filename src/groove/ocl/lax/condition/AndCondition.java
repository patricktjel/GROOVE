package groove.ocl.lax.condition;

import java.util.Map;

public class AndCondition implements Condition {
    private Condition expr1;
    private Condition expr2;

    public AndCondition(Condition expr1, Condition expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public Condition getExpr1() {
        return expr1;
    }

    public Condition getExpr2() {
        return expr2;
    }

    @Override
    public void renameVar(String o, String n) {
        expr1.renameVar(o, n);
        expr2.renameVar(o, n);
    }

    @Override
    public Map<String, String> simplify() {
        Map<String, String> eqEdges1 = expr1.simplify();
        if(expr1 instanceof LaxCondition && ((LaxCondition) expr1).getGraph().edgeSet().isEmpty()) {
            // if expr1 was an equivalence graph remove it
            expr1 = null;
        }

        Map<String, String> eqEdges2 = expr2.simplify();
        if(expr2 instanceof LaxCondition && ((LaxCondition) expr2).getGraph().edgeSet().isEmpty()) {
            // if expr2 was an equivalence graph remove it
            expr2 = null;
        }

        eqEdges1.putAll(eqEdges2);
        return eqEdges1;
    }

    @Override
    public String toString() {
        return String.format("%s \u2227 %s", expr1, expr2);
    }
}
