import groove.graph.plain.PlainGraph;
import groove.ocl.Groove;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.Quantifier;
import groove.ocl.lax.condition.AndCondition;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EquivalenceTests {


    // E1
    @Test
    public void simplifyDoubleExistential_g1EQg2() throws Exception {
        PlainGraph g1 = GraphBuilder.createGraph();
        g1.setName("g1");
        GraphBuilder.addNode(g1, "p", "person");

        PlainGraph g2 = GraphBuilder.cloneGraph(g1);
        g2.setName("g2");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g2);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g1, l1);

        condition.simplify();

        assertEquals(new LaxCondition(Quantifier.EXISTS, g1).toString(), condition.toString());
    }

    // E1C
    @Test
    public void simplify2Existential_g1subg2() throws Exception {
        PlainGraph g1 = GraphBuilder.createGraph();
        g1.setName("g1");
        GraphBuilder.addNode(g1, "p", "person");

        PlainGraph g2 = GraphBuilder.cloneGraph(g1);
        g2.setName("g2");
        GraphBuilder.addNode(g2, "m", "mother");
        GraphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g2);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g1, l1);

        condition.simplify();

        assertEquals(new LaxCondition(Quantifier.EXISTS, g2).toString(), condition.toString());
    }

    // E1C
    @Test
    public void simplify2Existential_g2subg1() throws Exception {
        PlainGraph g1 = GraphBuilder.createGraph();
        g1.setName("g1");
        GraphBuilder.addNode(g1, "p", "person");

        PlainGraph g2 = GraphBuilder.cloneGraph(g1);
        g2.setName("g2");
        GraphBuilder.addNode(g2, "m", "mother");
        GraphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g1);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g2, l1);

        condition.simplify();

        assertEquals(new LaxCondition(Quantifier.EXISTS, g2).toString(), condition.toString());
    }

    //E1B
    @Test
    public void simplify2Existential_g1capg2() throws Exception {
        PlainGraph g1 = GraphBuilder.createGraph();
        g1.setName("g1");
        GraphBuilder.addNode(g1, "p", "person");
        GraphBuilder.addNode(g1, "f", "father");
        GraphBuilder.addEdge(g1, "p", "f","f");

        PlainGraph g2 = GraphBuilder.createGraph();
        g2.setName("g2");
        GraphBuilder.addNode(g2, "p", "person");
        GraphBuilder.addNode(g2, "m", "mother");
        GraphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g1);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g2, l1);

        condition.simplify();

        PlainGraph graph = GraphBuilder.createGraph();
        GraphBuilder.addNode(graph, "p", "person");

        GraphBuilder.addNode(graph, "m", "mother");
        GraphBuilder.addEdge(graph, "p", "c", "m");

        GraphBuilder.addNode(graph, "f", "father");
        GraphBuilder.addEdge(graph, "p", "f","f");

        assertEquals(new LaxCondition(Quantifier.EXISTS, graph).toString(), condition.toString());
    }

    //E2
    @Test
    public void simplifyAndCondition() throws Exception {
        PlainGraph g = GraphBuilder.createGraph();
        g.setName("g");
        GraphBuilder.addNode(g, "p", "person");

        PlainGraph g1 = GraphBuilder.createGraph();
        g1.setName("g1");
        GraphBuilder.addNode(g1, "p", "person");
        GraphBuilder.addNode(g1, "f", "father");
        GraphBuilder.addEdge(g1, "p", "f","f");

        PlainGraph g2 = GraphBuilder.createGraph();
        g2.setName("g2");
        GraphBuilder.addNode(g2, "p", "person");
        GraphBuilder.addNode(g2, "m", "mother");
        GraphBuilder.addEdge(g2, "p", "c","m");

        LaxCondition l1 = new LaxCondition(Quantifier.EXISTS, g1);
        LaxCondition l2 = new LaxCondition(Quantifier.EXISTS, g2);

        AndCondition and = new AndCondition(l1, l2);
        LaxCondition condition = new LaxCondition(Quantifier.EXISTS, g, and);

        condition.simplify();

        PlainGraph graph = GraphBuilder.createGraph();
        GraphBuilder.addNode(graph, "p", "person");

        GraphBuilder.addNode(graph, "f", "father");
        GraphBuilder.addEdge(graph, "p", "f","f");

        GraphBuilder.addNode(graph, "m", "mother");
        GraphBuilder.addEdge(graph, "p", "c", "m");

        assertEquals(new LaxCondition(Quantifier.EXISTS, graph).toString(), condition.toString());
    }

    // E3
    @Test
    public void test() throws Exception {
        PlainGraph g1 = GraphBuilder.createGraph();
        GraphBuilder.addNode(g1, "self", "person");

        PlainGraph g2 = GraphBuilder.createGraph();
        GraphBuilder.addNode(g2, "n0", "person");

        PlainGraph g3 = GraphBuilder.createGraph();
        GraphBuilder.addNode(g3, "self", "person");
        GraphBuilder.addNode(g3, "n0", "person");
        GraphBuilder.addEdge(g3, "n0", Groove.EQ, "self");

        LaxCondition l3 = new LaxCondition(Quantifier.EXISTS, g3);
        LaxCondition l2 = new LaxCondition(Quantifier.EXISTS, g2, l3);
        LaxCondition condition = new LaxCondition(Quantifier.FORALL, g1, l2);

        condition.simplify();

        PlainGraph graph = GraphBuilder.createGraph();
        GraphBuilder.addNode(graph, "self", "person");

        assertEquals(new LaxCondition(Quantifier.FORALL, graph).toString(), condition.toString());
    }
}
