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

    public void setGraph(PlainGraph graph) {
        this.graph = graph;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
