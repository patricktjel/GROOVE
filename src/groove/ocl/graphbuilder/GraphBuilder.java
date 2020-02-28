package groove.ocl.graphbuilder;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.type.TypeNode;
import groove.graph.GraphRole;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.ocl.GrammarStorage;
import groove.ocl.lax.Operator;
import groove.ocl.lax.condition.AndCondition;
import groove.ocl.lax.condition.Condition;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.lax.graph.constants.BooleanConstant;
import groove.ocl.lax.graph.constants.Constant;
import groove.util.Log;
import groovy.lang.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static groove.ocl.Groove.*;


public class GraphBuilder {
    private final static Logger LOGGER = Log.getLogger(GraphBuilder.class.getName());

    // <Graph, <nodeName, PlainNode>>
    private static Map<PlainGraph, Map<String, PlainNode>> graphNodeMap = new HashMap<>();
    private static int uniqueNode = 0;
    private static int uniqueGraph = 0;

    /**
     * Create a new Graph and create its corresponding empty NodeMap
     */
    public static PlainGraph createGraph() {
        PlainGraph graph = new PlainGraph(getUniqueGraphName(), GraphRole.RULE);
        graphNodeMap.put(graph, new HashMap<>());
        return graph;
    }

    /**
     * Create a new node and add the label of the node
     * The label of a node is implemented as a self loop
     * @param graph     The graph in which the node is created
     * @param nodeName  The name of the node
     * @param label     The label of the node
     * @return          The name of the node
     */
    public static String addNode(PlainGraph graph, String nodeName, String label) {
        Map<String, PlainNode> nodeMap = graphNodeMap.get(graph);
        if (!nodeMap.containsKey(nodeName)) {
            PlainNode node = graph.addNode();

            // if a label is given add it as an edge
            if (label != null) {
                if (!label.contains(":")) {
                    // if the label does not contain ":" then 'type:' should be added
                    label = String.format("%s:%s", TYPE, label);
                }
                graph.addEdge(node, label, node);
            }

            nodeMap.put(nodeName, node);
        }
        return nodeName;
    }

    public static String addNode(PlainGraph graph, String label) {
        return addNode(graph, getUniqueNodeName(), label);
    }

    /**
     * Add an edge from {@param from} to {@param to} with the label {@param label}
     */
    public static void addEdge(PlainGraph graph, String from, String label, String to) {
        Map<String, PlainNode> nodeMap = graphNodeMap.get(graph);
        graph.addEdge(nodeMap.get(from), label, nodeMap.get(to));
    }

    public static void removeEdge(PlainGraph graph, PlainEdge edge) {
        graph.removeEdge(edge);
    }

    public static void removeNode(PlainGraph graph, PlainNode node) {
        graph.removeNode(node);
        graphNodeMap.get(graph).remove(getVarName(graph, node));
    }

    public static void removeGraph(PlainGraph graph) {
        graphNodeMap.remove(graph);
    }

    /**
     * Given a graph, an old graph node name and a new graph node name
     * Check if the old graph node name exists in the current graph according to the graphNodeMap
     * If so replace the old graph node name with the new graph node name
     * @param graph     The graph
     * @param o         The old graph node name
     * @param n         The new graph node name
     */
    public static void renameVar(PlainGraph graph, String o, String n) {
        PlainNode node = graphNodeMap.get(graph).get(o);
        if (node != null) {
            // if the graph contains the old node, replace that name with the new name
            graphNodeMap.get(graph).remove(o);
            graphNodeMap.get(graph).put(n, node);
        }
    }

    /**
     * Given a PlainNode from the graph, return its variable name;
     */
    public static String getVarName(PlainGraph graph, PlainNode grooveName) {
        return graphNodeMap.get(graph).entrySet().stream()
                .filter(e -> e.getValue().equals(grooveName))
                .collect(Collectors.toList())
                .get(0)
                .getKey();
    }

    /**
     * Used to get the name of the variable of a variableGraph (v:T)
     * These graphs contain only one node
     */
    public static String getVarName(PlainGraph graph) {
        return (String) graphNodeMap.get(graph).keySet().toArray()[0];
    }

    /**
     * Given a variable Name (exists only in the code), determine its type by looking in the OutEdge set of the given variable name
     * @param key       The variable name
     * @return          The type of the variable
     *                  or null if the variable does not exists
     */
    public static String getVariableType(String key) {
        for (Map.Entry<PlainGraph, Map<String, PlainNode>> entry :graphNodeMap.entrySet()) {
            if (entry.getValue().containsKey(key)) {
                PlainNode node = entry.getValue().get(key);
                PlainEdge edge = entry.getKey().outEdgeSet(node).stream()
                        .filter(e -> e.label().toString().contains("type:"))
                        .collect(Collectors.toList())
                        .get(0);
                return edge.label().toString().replace(String.format("%s:",TYPE), "");
            }
        }
        // shouldn't happen unless method is called with a key that does not exist
        return null;
    }

    /**
     * Create a clone for the given graph, create a clone of the corresponding nodeMap
     * and rename the graph to put it as a unique graph in the graphNodeMap
     * @param graph     The graph to clone
     * @return          The cloned graph
     */
    public static PlainGraph cloneGraph(PlainGraph graph) {
        PlainGraph g = graph.clone();
        Map<String, PlainNode> nodeMap = cloneNodeMap(graph);

        g.setName(getUniqueGraphName());
        graphNodeMap.put(g, nodeMap);
        return g;
    }

    /**
     * Create a clone of the NodeMap of a given Graph
     * @param graph     The graph
     * @return          The cloned NodeMap
     */
    public static Map<String, PlainNode> cloneNodeMap(PlainGraph graph) {
        return graphNodeMap.get(graph).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Saves the created graph
     */
    public static void save(PlainGraph graph) {
        GrammarStorage.saveGraph(AspectGraph.newInstance(graph));
    }

    /**
     * Given a graph in which v:T is already defined add the attributed graph components
     * (rule 17)
     */
    public static void addAttributedGraph(PlainGraph graph, String varName, Tuple2<String, TypeNode> attrType, Operator op, Constant n) {
        String aType = attrType.getSecond().text();
        String attr = addNode(graph, aType);

        // connect attribute to the v:T
        addEdge(graph, varName, attrType.getFirst(), attr);

        // create the constant node
        String attr2 = addNode(graph, n.getGrooveString());

        // create production nodes
        String operator = op.getGrooveString(aType);
        String prod = addNode(graph, String.format("%s:", PROD));
        String bool = addNode(graph, BooleanConstant.TRUE.getGrooveString());

        //connect production nodes
        addEdge(graph, prod, "arg:0", attr);
        addEdge(graph, prod, "arg:1", attr2);
        addEdge(graph, prod, operator, bool);
    }

    /**
     * Given a LaxCondition merge the recursive laxConditions into one graph including the quantification
     * @param c     The LaxCondition
     * @return      The resulting graph
     */
    public static PlainGraph laxToGraph(LaxCondition c) {
        return laxToGraph(c.getGraph(), c, 0);
    }

    private static PlainGraph laxToGraph(PlainGraph graph, LaxCondition c, int level) {
        Map<String, PlainNode> nodeMap = new HashMap<>();
        // add the graph to the existing graph
        if (level > 0 ) {
            nodeMap = cloneNodeMap(graph);
            mergeGraphs(graph, c.getGraph());
        }

        // create the quantification of this laxCondition
        String quantLvl = Integer.toString(level);
        addNode(graph, quantLvl, c.getQuantifier().getGrooveString());
        // create connection between the current quantification level and the previous quantification level
        if (level > 0) {
            addEdge(graph, quantLvl, IN, Integer.toString(level-1));
        }

        // connect the nodes of the graph with its quantifier
        for (Map.Entry<String, PlainNode> entry : graphNodeMap.get(c.getGraph()).entrySet()) {
            if (!entry.getKey().equals(quantLvl) && !nodeMap.containsKey(entry.getKey())) {
                //the quantifier shouldn't connect with itself and check if the node didn't exist already in the previous quantlevel
                addEdge(graph, entry.getKey(), AT, quantLvl);
            }
        }

        // all nodes from this level are created and connected. Start with the next level if applicable
        if (c.getCondition() != null) {
            return laxToGraphCondition(graph, c.getCondition(), level + 1);
        } else {
            return graph;
        }
    }

    /**
     * For an and condition both conditions should be handled
     */
    private static PlainGraph laxToGraph(PlainGraph graph, AndCondition c, int level) {
        laxToGraphCondition(graph, c.getExpr1(), level);
        laxToGraphCondition(graph, c.getExpr2(), level);
        return graph;
    }

    /**
     * Determine if the Condition is an LaxCondition or an AndCondition
     * Cast the condition to the right type and call the right method
     */
    private static PlainGraph laxToGraphCondition(PlainGraph graph, Condition c, int level) {
        if (c instanceof LaxCondition) {
            return laxToGraph(graph, (LaxCondition) c, level);
        } else {
            return laxToGraph(graph, (AndCondition) c, level);
        }
    }

    /**
     * Given two graphs merge the second graph <code>g2</code> into the first graph <code>g1</code>
     * By adding all nodes of g2, not existing in g1 to g1
     * and connect all edges existing in g2, not existing in g1 to g1
     * @param g1    The first graph
     * @param g2    The second graph
     */
    private static void mergeGraphs(PlainGraph g1, PlainGraph g2) {
        // Create all nodes of g2 in g1
        // AddNode does check if the node does exist already, if so it doesn't create a new one
        for (Map.Entry<String, PlainNode> entry: graphNodeMap.get(g2).entrySet()){
            addNode(g1, entry.getKey(), null);
        }

        // create all edges of g2 in g1
        for (PlainEdge edge: g2.edgeSet()) {
            if (!g1.containsEdge(edge)) {
                addEdge(g1, getVarName(g2, edge.source()), edge.label().text(), getVarName(g2, edge.target()));
            }
        }
    }

    /**
     * An helper function to generate a Unique Graph Name ("0")
     */
    private static String getUniqueGraphName() {
        return String.format("g%d", uniqueGraph++);
    }

    /**
     * An helper function to generate a Unique Node name ("n0")
     */
    private static String getUniqueNodeName() {
        return String.format("n%d", uniqueNode++);
    }

    /**
     * An helper function that connects the Groove names within one specific graph with the variable names (and generated names)
     * Of the different Lax conditions such that not every graph starts with a node n0
     */
    public static String graphToString(PlainGraph graph) {
        String result = graph.edgeSet().toString();
        for (Map.Entry<String, PlainNode> v : graphNodeMap.get(graph).entrySet()) {
            result = result.replace(v.getValue().toString(),  v.getKey());
        }
        return result;
    }
}