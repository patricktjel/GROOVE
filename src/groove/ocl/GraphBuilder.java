package groove.ocl;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.grammar.model.ResourceKind;
import groove.graph.GraphRole;
import groove.io.store.SystemStore;
import groove.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GraphBuilder {
    private final static Logger LOGGER = Log.getLogger(OCLParser.class.getName());

    private AspectGraph graph;
    private int nodeNumber;
    private Map<String, AspectNode> nodeMap;

    private static final String graphLocation = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\test.gps";
    private SystemStore store;
    /**
     * Creates or overwrites the graph with the name {@param ruleName}
     * And provides helper methods to make it easier to create graphs of type Rule conditions.
     */
    public GraphBuilder(String graphName) {
        graph = new AspectGraph(graphName, GraphRole.RULE);
        nodeNumber = 0;
        nodeMap = new HashMap<>();
        try {
            store = SystemStore.newStore(new File(graphLocation), false);
        } catch (IOException e) {
            LOGGER.severe("Could not load graph grammar");
        }
    }

    /**
     * Create a new node and add the label of the node
     *      The lable of a node is implemented as a self loop.
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
        try {
            store.reload();
            store.putGraphs(ResourceKind.RULE, Collections.singleton(graph), false);
            LOGGER.info(String.format("Rule graph: %s is saved", graph.getQualName()));
        } catch (IOException e) {
            LOGGER.warning(String.format("Could not save: %s", graph.getQualName()));
            e.printStackTrace();
        }
    }

}
