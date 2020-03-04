package groove.ocl.lax;

import groove.graph.plain.PlainGraph;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.AndCondition;
import groove.ocl.lax.condition.Condition;
import groove.ocl.lax.condition.LaxCondition;

import java.util.Map;
import java.util.stream.Collectors;

import static groove.ocl.Groove.EQ;

/**
 * A class responsible for simplifying a LaxCondition
 */
public class LaxSimplifier {

    private GraphBuilder graphBuilder;

    public LaxSimplifier(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    /**
     * A method that makes sure that the correct simplify method is called
     * @return      A list with equivalences according to E3
     */
    public Map<String, String> simplify (Condition condition) {
        if (condition instanceof LaxCondition) {
            return simplify((LaxCondition) condition);
        } else if (condition instanceof AndCondition) {
            return simplify((AndCondition) condition);
        }
        //shouldn't happen
        assert false;
        return null;
    }

    /**
     * Apply the equivalence rules to make the Lax condition simple
     * @return  The final LaxCondition result
     */
    @SuppressWarnings("DanglingJavadoc") // Necessary for method reference in Javadoc
    private Map<String, String> simplify(LaxCondition laxCon) {
        Map<String, String> eqEdges;
        if (laxCon.getCondition() != null) {
            eqEdges = simplify(laxCon.getCondition());

            if (laxCon.getCondition() instanceof LaxCondition && ((LaxCondition) laxCon.getCondition()).getGraph().edgeSet().isEmpty()) {
                // if condition was an equivalence graph remove it
                laxCon.setCondition(null);
            }
        } else {
            // no condition below us anymore so check if there are equivalence edges
            eqEdges = laxCon.getGraph().edgeSet().stream()
                    .filter(e -> e.label().toParsableString().equals(EQ))
                    .collect(Collectors.toMap(
                            e -> graphBuilder.getVarName(laxCon.getGraph(), e.source()),
                            e -> graphBuilder.getVarName(laxCon.getGraph(), e.target())
                    ));
            if (!eqEdges.isEmpty()) {
                // This graph defines equivalence edges
                graphBuilder.removeGraph(laxCon.getGraph());
                laxCon.getGraph().removeEdgeSet(laxCon.getGraph().edgeSet());
            }
            return eqEdges;
        }

        // replace AndCondition if possible (part of E3)
        if (laxCon.getCondition() instanceof AndCondition) {
            replaceAndCondition(laxCon);
        }

        // Apply E3 replacement
        for (Map.Entry<String, String> entry :eqEdges.entrySet()) {
            renameVar(laxCon, entry.getKey(), entry.getValue());
        }

        // if the condition is an AndCondition Let it simplify according to E2
        if (laxCon.getCondition() instanceof AndCondition) {
            laxCon.setCondition(simplifyE2((AndCondition) laxCon.getCondition()));
        }

        // If condition (and this) are LaxConditions and the quantifiers bot existential, try to apply E1 rules
        if (laxCon.getCondition() instanceof LaxCondition && laxCon.getQuantifier().equals(Quantifier.EXISTS)
                && ((LaxCondition) laxCon.getCondition()).getQuantifier().equals(Quantifier.EXISTS)){
            LaxCondition laxCon2 = (LaxCondition) laxCon.getCondition();
            laxCon.setGraph(graphBuilder.mergeGraphs(laxCon.getGraph(), laxCon2.getGraph()));
            laxCon.setCondition(laxCon2.getCondition());
        }

        /** If the condition is null && the graphs are the same (according to {@link GraphBuilder#graphToString(PlainGraph graph)}) && the quantification is the same
         * or the outer quantification is bigger, then they may be merged together */
        if (laxCon.getCondition() instanceof LaxCondition
                && graphBuilder.graphToString(laxCon.getGraph()).equals(graphBuilder.graphToString(((LaxCondition) laxCon.getCondition()).getGraph()))
                && laxCon.getQuantifier().compareTo(((LaxCondition) laxCon.getCondition()).getQuantifier()) >= 0) {
            laxCon.setCondition(((LaxCondition) laxCon.getCondition()).getCondition());
        }

        return eqEdges;
    }

    /**
     * Handle the simplify for the AndCondition
     */
    private Map<String, String> simplify(AndCondition andCon) {
        Map<String, String> eqEdges1 = simplify(andCon.getExpr1());
        if(andCon.getExpr1() instanceof LaxCondition && ((LaxCondition) andCon.getExpr1()).getGraph().edgeSet().isEmpty()) {
            // if expr1 was an equivalence graph remove it
            andCon.setExpr1(null);
        }

        Map<String, String> eqEdges2 = simplify(andCon.getExpr2());
        if(andCon.getExpr2() instanceof LaxCondition && ((LaxCondition) andCon.getExpr2()).getGraph().edgeSet().isEmpty()) {
            // if expr2 was an equivalence graph remove it
            andCon.setExpr2(null);
        }

        eqEdges1.putAll(eqEdges2);
        return eqEdges1;
    }

    /**
     * Given an AndCondition apply the Equivalences E2 and return the resul
     * @param   andCon  AndCondition
     * @return          The result of applying E2 rules
     */
    private Condition simplifyE2(AndCondition andCon) {
        // First try to simplify the existing AndConditions according to E2
        if (andCon.getExpr1() instanceof AndCondition){
            andCon.setExpr1(simplifyE2((AndCondition) andCon.getExpr1()));
        }
        if (andCon.getExpr2() instanceof AndCondition){
            andCon.setExpr2(simplifyE2((AndCondition) andCon.getExpr2()));
        }
        // so now we are sure that both expr1 and expr 2 are LaxConditions
        LaxCondition expr1L = (LaxCondition) andCon.getExpr1();
        LaxCondition expr2L = (LaxCondition) andCon.getExpr2();
        if (expr1L.getQuantifier().equals(Quantifier.EXISTS)
                && expr2L.getQuantifier().equals(Quantifier.EXISTS)) {
            PlainGraph graph = graphBuilder.mergeGraphs(expr1L.getGraph(), expr2L.getGraph());
            return new LaxCondition(Quantifier.EXISTS, graph);
        }
        // we can't simplify according to E2 so return the current AndCondition
        return andCon;
    }

    /**
     * Checks if both conditions are null of either one of them such that it can be replaced
     */
    private void replaceAndCondition(LaxCondition laxCon){
        AndCondition c = (AndCondition) laxCon.getCondition();
        if (c.getExpr1() == null && c.getExpr2() == null){
            laxCon.setCondition(null);
        } else if (c.getExpr1() == null) {
            laxCon.setCondition(c.getExpr2());
        } else if (c.getExpr2() == null) {
            laxCon.setCondition(c.getExpr1());
        }
        // else both != null ,the and condition will stay
    }

    /**
     * A method that makes sure that the correct renameVar method is called
     */
    public void renameVar(Condition condition, String o, String n) {
        if (condition instanceof LaxCondition) {
            renameVar((LaxCondition) condition, o, n);
        } else if (condition instanceof AndCondition) {
            renameVar((AndCondition) condition, o, n);
        }
    }

    /**
     * Apply the renameVar and if condition != null apply recursively.
     */
    private void renameVar(LaxCondition laxCon, String o, String n) {
        graphBuilder.renameVar(laxCon.getGraph(), o, n);
        if (laxCon.getCondition() != null) {
            renameVar(laxCon.getCondition(), o, n);
        }
    }

    /**
     * Call renameVar for both expr1 and expr2
     */
    private void renameVar(AndCondition andCon, String o, String n) {
        renameVar(andCon.getExpr1(), o, n);
        renameVar(andCon.getExpr2(), o, n);
    }
}
