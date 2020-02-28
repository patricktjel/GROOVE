package groove.ocl.lax.condition;

import groove.graph.plain.PlainGraph;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.Quantifier;

import java.util.Map;
import java.util.stream.Collectors;

import static groove.ocl.Groove.EQ;

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
    @SuppressWarnings("DanglingJavadoc") // Necessary for method reference in Javadoc
    public Map<String, String> simplify() {
        Map<String, String> eqEdges;
        if (condition != null) {
            eqEdges = condition.simplify();

            if (condition instanceof LaxCondition && ((LaxCondition) condition).getGraph().edgeSet().isEmpty()) {
                // if condition was an equivalence graph remove it
                condition = null;
            }
        } else {
            // no condition below us anymore so check if there are equivalence edges
            eqEdges = graph.edgeSet().stream()
                    .filter(e -> e.label().toParsableString().equals(EQ))
                    .collect(Collectors.toMap(
                            e -> GraphBuilder.getVarName(graph, e.source()),
                            e -> GraphBuilder.getVarName(graph, e.target())
                    ));
            if (!eqEdges.isEmpty()) {
                // This graph defines equivalence edges
                GraphBuilder.removeGraph(graph);
                graph.removeEdgeSet(graph.edgeSet());
            }
            return eqEdges;
        }

        // replace AndCondition if possible (part of E3)
        if (condition instanceof AndCondition) {
            replaceAndCondition();
        }

        // Apply E3 replacement
        for (Map.Entry<String, String> entry :eqEdges.entrySet()) {
            renameVar(entry.getKey(), entry.getValue());
        }

        // if the condition is an AndCondition Let it simplify according to E2
        if (condition instanceof AndCondition) {
            condition = ((AndCondition) condition).simplifyE2();
        }

        // If condition (and this) are LaxConditions and the quantifiers bot existential, try to apply E1 rules
        if (condition instanceof LaxCondition && quantifier.equals(Quantifier.EXISTS)
                && ((LaxCondition) condition).getQuantifier().equals(Quantifier.EXISTS)){
            LaxCondition laxCon = (LaxCondition) condition;
            graph = GraphBuilder.mergeGraphs(graph, laxCon.getGraph());
            condition = laxCon.getCondition();
        }

        /** If the condition is null && the graphs are the same (according to {@link GraphBuilder#graphToString(PlainGraph graph)}) && the quantification is the same
         * or the outer quantification is bigger, then they may be merged together */
        if (condition instanceof LaxCondition
                && GraphBuilder.graphToString(graph).equals(GraphBuilder.graphToString(((LaxCondition) condition).getGraph()))
                && quantifier.compareTo(((LaxCondition) condition).getQuantifier()) >= 0) {
            condition = ((LaxCondition) condition).getCondition();
        }

        return eqEdges;
    }

    /**
     * Checks if both conditions are null of either one of them such that it can be replaced
     */
    private void replaceAndCondition(){
        AndCondition c = (AndCondition) condition;
        if (c.getExpr1() == null && c.getExpr2() == null){
            condition = null;
        } else if (c.getExpr1() == null) {
            condition = c.getExpr2();
        } else if (c.getExpr2() == null) {
            condition = c.getExpr1();
        }
        // else both != null ,the and condition will stay
    }

    @Override
    public void renameVar(String o, String n) {
        GraphBuilder.renameVar(graph, o, n);
        if (condition != null) {
            condition.renameVar(o, n);
        }
    }

    @Override
    public String toString() {
        String g = GraphBuilder.graphToString(graph);
        if (condition == null) {
            return String.format("%s(%s)", quantifier, g);
        } else {
            return String.format("%s(%s, %s)", quantifier, g, condition);
        }
    }
}
