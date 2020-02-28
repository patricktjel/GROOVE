package groove.ocl.lax.condition;

import groove.graph.plain.PlainGraph;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.Quantifier;

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

    public Condition simplifyE2() {
        // First try to simplify the existing AndConditions according to E2
        if (expr1 instanceof AndCondition){
            expr1 = ((AndCondition) expr1).simplifyE2();
        }
        if (expr2 instanceof AndCondition){
            expr2 = ((AndCondition) expr2).simplifyE2();
        }
        // so now we are sure that both expr1 and expr 2 are LaxConditions
        LaxCondition expr1L = (LaxCondition) expr1;
        LaxCondition expr2L = (LaxCondition) expr2;
        if (expr1L.getQuantifier().equals(Quantifier.EXISTS)
                && expr2L.getQuantifier().equals(Quantifier.EXISTS)) {
            PlainGraph graph = GraphBuilder.mergeGraphs(expr1L.getGraph(), expr2L.getGraph());
            return new LaxCondition(Quantifier.EXISTS, graph);
        }
        // we can't simplify according to E2 so return the current AndCondition
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s \u2227 %s", expr1, expr2);
    }
}
