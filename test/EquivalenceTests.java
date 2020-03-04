import groove.graph.plain.PlainGraph;
import groove.ocl.Groove;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.LaxSimplifier;
import groove.ocl.lax.Quantifier;
import groove.ocl.lax.condition.AndCondition;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("RedundantThrows")
public class EquivalenceTests {

    private GraphBuilder graphBuilder = new GraphBuilder();
    private LaxSimplifier laxSimplifier = new LaxSimplifier(graphBuilder);

    // E1
    @Test
    public void simplifyDoubleExistential_g1EQg2() throws Exception {
        PlainGraph g1 = graphBuilder.createGraph();
        graphBuilder.addNode(g1, "p", "person");

        PlainGraph g2 = graphBuilder.cloneGraph(g1);

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g2);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g1, l1);

        laxSimplifier.simplify(condition);

        String expected = graphBuilder.conToString(new LaxCondition(Quantifier.EXISTS, g1));
        assertEquals(expected, graphBuilder.conToString(condition));
    }

    // E1C
    @Test
    public void simplify2Existential_g1subg2() throws Exception {
        PlainGraph g1 = graphBuilder.createGraph();
        graphBuilder.addNode(g1, "p", "person");

        PlainGraph g2 = graphBuilder.cloneGraph(g1);
        graphBuilder.addNode(g2, "m", "mother");
        graphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g2);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g1, l1);

        laxSimplifier.simplify(condition);

        String expected = graphBuilder.conToString(new LaxCondition(Quantifier.EXISTS, g2));
        assertEquals(expected, graphBuilder.conToString(condition));
    }

    // E1C
    @Test
    public void simplify2Existential_g2subg1() throws Exception {
        PlainGraph g1 = graphBuilder.createGraph();
        graphBuilder.addNode(g1, "p", "person");

        PlainGraph g2 = graphBuilder.cloneGraph(g1);
        graphBuilder.addNode(g2, "m", "mother");
        graphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g1);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g2, l1);

        laxSimplifier.simplify(condition);

        String expected = graphBuilder.conToString(new LaxCondition(Quantifier.EXISTS, g2));
        assertEquals(expected, graphBuilder.conToString(condition));
    }

    //E1B
    @Test
    public void simplify2Existential_g1capg2() throws Exception {
        PlainGraph g1 = graphBuilder.createGraph();
        graphBuilder.addNode(g1, "p", "person");
        graphBuilder.addNode(g1, "f", "father");
        graphBuilder.addEdge(g1, "p", "f","f");

        PlainGraph g2 = graphBuilder.createGraph();
        graphBuilder.addNode(g2, "p", "person");
        graphBuilder.addNode(g2, "m", "mother");
        graphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g1);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g2, l1);

        laxSimplifier.simplify(condition);

        PlainGraph graph = graphBuilder.createGraph();
        graphBuilder.addNode(graph, "p", "person");

        graphBuilder.addNode(graph, "m", "mother");
        graphBuilder.addEdge(graph, "p", "c", "m");

        graphBuilder.addNode(graph, "f", "father");
        graphBuilder.addEdge(graph, "p", "f","f");

        String expected = graphBuilder.conToString(new LaxCondition(Quantifier.EXISTS, graph));
        assertEquals(expected, graphBuilder.conToString(condition));
    }

    //E2 (and E1 to merge it with the first existential)
    @Test
    public void simplifyAndCondition() throws Exception {
        PlainGraph g = graphBuilder.createGraph();
        graphBuilder.addNode(g, "p", "person");

        PlainGraph g1 = graphBuilder.createGraph();
        graphBuilder.addNode(g1, "p", "person");
        graphBuilder.addNode(g1, "f", "father");
        graphBuilder.addEdge(g1, "p", "f","f");

        PlainGraph g2 = graphBuilder.createGraph();
        graphBuilder.addNode(g2, "p", "person");
        graphBuilder.addNode(g2, "m", "mother");
        graphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g1);
        LaxCondition l2 = new LaxCondition(Quantifier.EXISTS, g2);

        AndCondition and = new AndCondition(l1, l2);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g, and);

        laxSimplifier.simplify(condition);

        PlainGraph graph = graphBuilder.createGraph();
        graphBuilder.addNode(graph, "p", "person");

        graphBuilder.addNode(graph, "f", "father");
        graphBuilder.addEdge(graph, "p", "f","f");

        graphBuilder.addNode(graph, "m", "mother");
        graphBuilder.addEdge(graph, "p", "c", "m");

        String expected = graphBuilder.conToString(new LaxCondition(Quantifier.EXISTS, graph));
        assertEquals(expected, graphBuilder.conToString(condition));
    }

    // E3
    @Test
    public void test() throws Exception {
        PlainGraph g1 = graphBuilder.createGraph();
        graphBuilder.addNode(g1, "self", "person");

        PlainGraph g2 = graphBuilder.createGraph();
        graphBuilder.addNode(g2, "n0", "person");

        PlainGraph g3 = graphBuilder.createGraph();
        graphBuilder.addNode(g3, "self", "person");
        graphBuilder.addNode(g3, "n0", "person");
        graphBuilder.addEdge(g3, "n0", Groove.EQ, "self");

        LaxCondition l3 = new LaxCondition(Quantifier.EXISTS, g3);
        LaxCondition l2 = new LaxCondition(Quantifier.EXISTS, g2, l3);
        LaxCondition condition = new LaxCondition(Quantifier.FORALL, g1, l2);

        laxSimplifier.simplify(condition);

        PlainGraph graph = graphBuilder.createGraph();
        graphBuilder.addNode(graph, "self", "person");

        String expected = graphBuilder.conToString(new LaxCondition(Quantifier.FORALL, graph));
        assertEquals(expected, graphBuilder.conToString(condition));
    }
}
