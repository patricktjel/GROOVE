package groove.ocl.graphbuilder;

import groove.grammar.type.TypeNode;
import groove.graph.GraphRole;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.ocl.lax.Operator;
import groove.ocl.lax.graph.constants.BooleanConstant;
import groove.ocl.lax.graph.constants.Constant;
import groove.util.Log;
import groovy.lang.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GraphBuilder {
    private final static Logger LOGGER = Log.getLogger(GraphBuilder.class.getName());

    private final static String AT = "@";
    private final static String PROD = "prod:";
    private final static String IN = "in";
    private final static String TYPE = "type:";

    // <graphName, <nodeName, PlainNode>>
    private Map<PlainGraph, Map<String, PlainNode>> graphNodeMap;
    // <varName(nodeName), type>
    private int unique_name;

    /**
     * Creates or overwrites the graph with the name {@param ruleName}
     * And provides helper methods to make it easier to create graphs of type Rule conditions.
     */
    public GraphBuilder() {
        this.graphNodeMap = new HashMap<>();
        this.unique_name = 0;
    }

    public PlainGraph createGraph() {
        PlainGraph graph = new PlainGraph(getUniqueName(), GraphRole.NONE);
        graphNodeMap.put(graph, new HashMap<>());
        return graph;
    }

    /**
     * Create a new node and add the label of the node
     *      The label of a node is implemented as a self loop.
     * @param label     The name of a node
     */
    public String addNode(PlainGraph graph, String nodeName, String label) {
        Map<String, PlainNode> nodeMap = graphNodeMap.get(graph);
        if (!nodeMap.containsKey(nodeName)) {
            PlainNode node = graph.addNode();
            String type = String.format("%s%s", TYPE, label);
            graph.addEdge(node, type, node);

            nodeMap.put(nodeName, node);
        }
        return nodeName;
    }

    public String addNode(PlainGraph graph, String label) {
        return this.addNode(graph, getUniqueName(), label);
    }

    /**
     * Add an edge from {@param from} to {@param to} with the label {@param label}
     */
    public void addEdge(PlainGraph graph, String from, String label, String to) {
        Map<String, PlainNode> nodeMap = graphNodeMap.get(graph);
        graph.addEdge(nodeMap.get(from), label, nodeMap.get(to));
    }

    public void addAttributedGraph(PlainGraph graph, String varName, Tuple2<String, TypeNode> attrType, Operator op, Constant n) {
        String aType = attrType.getSecond().text();
        String attr = addNode(graph, aType);

        // connect attribute to the v:T
        addEdge(graph, varName, attrType.getFirst(), attr);

        // create the constant node
        String attr2 = addNode(graph, n.getGrooveString());

        // create production nodes
        String operator = op.getGrooveString(aType);
        String prod = addNode(graph, String.format("%s", PROD));
        String bool = addNode(graph, BooleanConstant.TRUE.getGrooveString());

        //connect production nodes
        addEdge(graph, prod, "arg:0", attr);
        addEdge(graph, prod, "arg:1", attr2);
        addEdge(graph, prod, operator, bool);
    }

    public String getVariable(String key) {
        for (Map.Entry<PlainGraph, Map<String, PlainNode>> entry :graphNodeMap.entrySet()) {
            if (entry.getValue().containsKey(key)) {
                PlainNode node = entry.getValue().get(key);
                PlainEdge edge = entry.getKey().outEdgeSet(node).stream()
                        .filter(e -> e.label().toString().contains("type:"))
                        .collect(Collectors.toList())
                        .get(0);
                return edge.label().toString().replace(TYPE, "");
            }
        }
        // shouldn't happen unless method is called with a key that does not exist
        return null;
    }

    public PlainGraph cloneGraph(PlainGraph graph) {
        PlainGraph g = graph.clone();

        //clone the map
        Map<String, PlainNode> nodeMap = graphNodeMap.get(graph).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        g.setName(getUniqueName());
        graphNodeMap.put(g, nodeMap);
        return g;
    }

    /**
     * Saves the created graph
     */
    public void save(PlainGraph graph) {
        //TODO fix the save
//        GrammarStorage.saveGraph(graph);
    }

    private String getUniqueName() {
        return String.valueOf(unique_name++);
    }
}
