package groove.ocl;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.graph.GraphRole;
import groove.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GraphBuilder {
    private final static Logger LOGGER = Log.getLogger(GraphBuilder.class.getName());

    private AspectGraph graph;
    private int nodeNumber;
    private Map<String, AspectNode> nodeMap;

    /**
     * Creates or overwrites the graph with the name {@param ruleName}
     * And provides helper methods to make it easier to create graphs of type Rule conditions.
     */
    public GraphBuilder(String graphName) {
        graph = new AspectGraph(graphName, GraphRole.RULE);
        nodeNumber = 0;
        nodeMap = new HashMap<>();
    }

    /**
     * Create a new node and add the label of the node
     *      The label of a node is implemented as a self loop.
     * @param label     The name of a node
     */
    public void addNode(String name, String label) {
        AspectNode node = new AspectNode(nodeNumber++, GraphRole.RULE);
        graph.addNode(node);
        addEdge(node, label, node);
        nodeMap.put(name, node);
    }

    /**
     * Add an edge from {@param from} to {@param to} with the label {@param label}
     */
    public void addEdge(AspectNode from, String label, AspectNode to) {
        AspectLabel l = new AspectLabel(GraphRole.RULE);
        l.setInnerText(label);
        graph.addEdge(from, label, to);
    }

    public void addEdge(String from, String label, String to) {
        addEdge(nodeMap.get(from), label, nodeMap.get(to));
    }

    /**
     * Saves the created graph
     */
    public void save() {
        GrammarStorage.saveGraph(graph);
    }
}
