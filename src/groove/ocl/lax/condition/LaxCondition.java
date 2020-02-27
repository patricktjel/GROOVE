package groove.ocl.lax.condition;

import groove.graph.plain.PlainGraph;
import groove.ocl.lax.Quantifier;

public class LaxCondition implements Condition {

    private Quantifier quantifier;
    private PlainGraph graph;
    private Condition condition;

    public LaxCondition(Quantifier quantifier, PlainGraph graph, Condition condition) {
        this.quantifier = quantifier;
        this.graph = graph;
        this.condition = condition;
    }

    public LaxCondition(Quantifier quantifier, PlainGraph graph) {
        this(quantifier, graph, null);
    }

    public Quantifier getQuantifier() {
        return quantifier;
    }

    public PlainGraph getGraph() {
        return graph;
    }

    public Condition getCondition() {
        return condition;
    }

    /**
     * Apply the equivalence rules to make the Lax condition simple
     * @return  The final LaxCondition result
     */
//    public boolean simplify() {
//        boolean toSimplify = false;
//        if (graph instanceof EquivVariable) {
//            // we have found an equivVariable. The Laxcondition above should start renaming.
//            return true;
//        }
//
//        // Keep simplifying
//        if (condition != null) {
//            toSimplify = condition.simplify();
//        }
//
//        // apply equivalence rules
//        //TODO: fix implementation
//        if (toSimplify) {
//            // if an EquivVariable is found we can apply E3
//            applyE3();
//        }
//
//        // If the condition is null, the expressions are the same and the quantification is the same
//        // or the outer quantification is bigger, then they may be merged together
//        if (this.condition instanceof LaxCondition && this.graph.equals(((LaxCondition) this.condition).getGraph())
//                && this.quantifier.compareTo(((LaxCondition) this.condition).getQuantifier()) >= 0) {
//            this.condition = ((LaxCondition) this.condition).getCondition();
//        }
//        return false;
//    }
//
//    /**
//     * Apply Equivalence rule 3
//     */
//    private void applyE3() {
//        EquivVariable equivVar = null;
//
//        if (condition instanceof LaxCondition) {
//            equivVar = (EquivVariable) ((LaxCondition) condition).getGraph();
//
//            // remove the equivVar from the condition
//            this.condition = null;
//        } else if (condition instanceof AndExpression){
//            equivVar = (EquivVariable) ((LaxCondition) ((AndExpression) condition).getExpr1()).getGraph();
//
//            // remove the equivVar from the condition
//            this.condition = ((AndExpression) condition).getExpr2();
//        }
//
//        assert equivVar != null; // If toSimplify is true there has to be an equivVar
//
//        // apply the equivalence declared in equivVar
//        this.renameVar(equivVar.getVar2(), equivVar.getVariableName());
//    }
//
//    @Override
//    public void renameVar(String o, String n) {
//        graph.renameVar(o, n);
//        if (condition != null) {
//            condition.renameVar(o, n);
//        }
//    }

    @Override
    public String toString() {
        if (condition == null) {
            return String.format("%s(%s)", quantifier, graph);
        } else {
            return String.format("%s(%s, %s)", quantifier, graph, condition);
        }
    }
}
