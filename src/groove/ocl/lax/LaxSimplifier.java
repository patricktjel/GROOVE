package groove.ocl.lax;

import groove.graph.plain.PlainGraph;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static groove.ocl.Groove.EQUIV;

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
     * @return      A list with equivalences according to E4
     */
    public Condition simplify (Condition condition) {
        if (condition instanceof LaxCondition) {
            return simplify((LaxCondition) condition);
        } else if (condition instanceof AndCondition) {
            return simplify((AndCondition) condition);
        } else if (condition instanceof OrCondition) {
            return simplify((OrCondition) condition);
        } else if (condition instanceof ImpliesCondition) {
            return simplify((ImpliesCondition) condition);
        }
        //shouldn't happen
        assert false;
        return null;
    }

    /**
     * Apply the equivalence rules to make the Lax condition simple
     * @return  The final LaxCondition result
     */
    private LaxCondition simplify(LaxCondition laxCon) {
        if (laxCon.getCondition() != null) {
            if (laxCon.getCondition() instanceof LaxCondition) {
                // check if the condition is an equivalence condition
                Map<String, String> eqEdges = getEquivalenceCondition((LaxCondition) laxCon.getCondition());
                if (!eqEdges.isEmpty()) {
                    // the condition is an equivalence condition
                    // apply E5
                    graphBuilder.renameVar(laxCon, eqEdges);
                    laxCon.setCondition(null);
                    // can't simplify further so return the condition
                    return laxCon;
                }
            } else if (laxCon.getCondition() instanceof AndCondition) {
                // check if the AndCondition contains an equivalence condition
                Map<String, String> eqEdges = getEquivalenceCondition((AndCondition) laxCon.getCondition());
                if (!eqEdges.isEmpty()) {
                    // the condition is an equivalence condition
                    // apply E4
                    graphBuilder.renameVar(laxCon, eqEdges);
                    if (((AndCondition) laxCon.getCondition()).getExpr1() == null
                            && ((AndCondition) laxCon.getCondition()).getExpr2() == null) {
                        laxCon.setCondition(null);
                        return laxCon;
                    } else if (((AndCondition) laxCon.getCondition()).getExpr1() == null) {
                        Condition c = ((AndCondition) laxCon.getCondition()).getExpr2();
                        graphBuilder.renameVar(c, eqEdges);
                        laxCon.setCondition(c);
                    } else if (((AndCondition) laxCon.getCondition()).getExpr2() == null) {
                        Condition c = ((AndCondition) laxCon.getCondition()).getExpr1();
                        graphBuilder.renameVar(c, eqEdges);
                        laxCon.setCondition(c);
                    } else {
                        assert false; // either one has to be null otherwise the eqEdges should have been empty
                    }
                }
            }
            laxCon.setCondition(simplify(laxCon.getCondition()));
        }

        // If condition (and this) are LaxConditions and the quantifiers are the same, try to apply E1 rules
        if (laxCon.getCondition() instanceof LaxCondition
                && laxCon.getQuantifier().equals(((LaxCondition) laxCon.getCondition()).getQuantifier())){
            LaxCondition laxCon2 = (LaxCondition) laxCon.getCondition();
            laxCon.setGraph(graphBuilder.mergeGraphs(laxCon.getGraph(), laxCon2.getGraph()));
            laxCon.setCondition(laxCon2.getCondition());
        }
        return laxCon;
    }

    /**
     * Get the EquivalenceConditions from the AndCondition if possible
     */
    private Map<String, String> getEquivalenceCondition(AndCondition con) {
        HashMap<String, String> result = new HashMap<>();
        if (con.getExpr1() instanceof LaxCondition) {
            Map<String, String> equiv1 = getEquivalenceCondition((LaxCondition) con.getExpr1());
            if (!equiv1.isEmpty()) {
                // if expr1 has an equivalence relation
                // remove the equivalence relation and return the rename
                con.setExpr1(null);
                result.putAll(equiv1);
            }
        }

        if (con.getExpr2() instanceof LaxCondition) {
            Map<String, String> equiv2 = getEquivalenceCondition((LaxCondition) con.getExpr2());
            if (!equiv2.isEmpty()) {
                // if expr2 has an equivalence relation
                // remove the equivalence relation and return the rename
                con.setExpr2(null);
                result.putAll(equiv2);
            }
        }
        // there are no equivalence relations
        return result;
    }

    /**
     * Get the Equivalence Condition from the LaxCondition if possible
     */
    private Map<String, String> getEquivalenceCondition(LaxCondition laxCon) {
        return laxCon.getGraph().edgeSet().stream()
                .filter(e -> e.label().toParsableString().equals(EQUIV))
                .collect(Collectors.toMap(
                        e -> graphBuilder.getVarName(laxCon.getGraph(), e.source()),
                        e -> graphBuilder.getVarName(laxCon.getGraph(), e.target())
                ));
    }

    /**
     * Handle the simplify for the AndCondition
     */
    private Condition simplify(AndCondition andCon) {
        // First try to simplify the existing conditions
        andCon.setExpr1(simplify(andCon.getExpr1()));
        andCon.setExpr2(simplify(andCon.getExpr2()));

        if (andCon.getExpr1() instanceof LaxCondition
                && andCon.getExpr2() instanceof LaxCondition) {
            // if both conditions are LaxConditions
            // check if the quantifiers are the same then try to apply E3
            LaxCondition expr1L = (LaxCondition) andCon.getExpr1();
            LaxCondition expr2L = (LaxCondition) andCon.getExpr2();
            if (expr1L.getQuantifier().equals(expr2L.getQuantifier())) {
                PlainGraph graph = graphBuilder.mergeGraphs(expr1L.getGraph(), expr2L.getGraph());
                return new LaxCondition(expr1L.getQuantifier(), graph);
            }
        }
        // we can't simplify according to E3 so return the current AndCondition
        return andCon;
    }

    /**
     * Handle the simplify for the OrCondition
     */
    private OrCondition simplify(OrCondition orCon) {
        orCon.setExpr1(simplify(orCon.getExpr1()));
        orCon.setExpr2(simplify(orCon.getExpr2()));
        return orCon;
    }

    /**
     * Handle the simplify for the ImpliesCondition
     */
    private LaxCondition simplify(ImpliesCondition impCon) {
        impCon.setExpr1(simplify(impCon.getExpr1()));
        impCon.setExpr2(simplify(impCon.getExpr2()));

        // Apply E2: E(a) implies E(b)== A(a, E(b))
        // TODO what if it is an OrCondition?
        assert impCon.getExpr1() instanceof LaxCondition;
        LaxCondition expr1 = (LaxCondition) impCon.getExpr1();
        assert expr1.getCondition() == null;

        return new LaxCondition(Quantifier.FORALL, expr1.getGraph(), impCon.getExpr2());
    }
}
