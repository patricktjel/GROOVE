package groove.ocl.graphbuilder;

import groove.grammar.type.TypeNode;
import groove.graph.GraphRole;
import groove.graph.plain.PlainEdge;
import groove.graph.plain.PlainGraph;
import groove.graph.plain.PlainNode;
import groove.ocl.lax.Operator;
import groove.ocl.lax.condition.*;
import groove.ocl.lax.graph.constants.BooleanConstant;
import groove.ocl.lax.graph.constants.Constant;
import groovy.lang.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

import static groove.ocl.Groove.*;

/**
 * A singleton class based on the static principle instead of using the GraphBuilder.getInstance() principle
 */
public class GraphBuilder {
    private static int uniqueGraph = 0;

    // <Graph, <nodeName, PlainNode>>
    private Map<PlainGraph, Map<String, PlainNode>> graphNodeMap = new HashMap<>();
    private int uniqueNode = 0;

    /**
     * Create a new Graph and create its corresponding empty NodeMap
     */
    public PlainGraph createGraph() {
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
    public String addNode(PlainGraph graph, String nodeName, String label) {
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

    public String addNode(PlainGraph graph, String label) {
        return addNode(graph, getUniqueNodeName(), label);
    }

    /**
     * Add an edge from {@param from} to {@param to} with the label {@param label}
     */
    public void addEdge(PlainGraph graph, String from, String label, String to) {
        Map<String, PlainNode> nodeMap = graphNodeMap.get(graph);
        addEdge(graph, nodeMap.get(from), label, nodeMap.get(to));
    }

    private void addEdge(PlainGraph graph, PlainNode from, String label, PlainNode to) {
        graph.addEdge(from, label, to);
    }

    public void removeEdge(PlainGraph graph, PlainEdge edge) {
        graph.removeEdge(edge);
    }

    public void removeNode(PlainGraph graph, PlainNode node) {
        graph.removeNode(node);
        graphNodeMap.get(graph).remove(getVarName(graph, node));
    }

    public void renameVar(Condition con, Map<String, String> eqEdges) {
        for (Map.Entry<String, String> entry : eqEdges.entrySet()) {
            renameVar(con, entry.getKey(), entry.getValue());
        }
    }

    /**
     * A method that makes sure that the correct renameVar method is called
     */
    public void renameVar(Condition condition, String o, String n) {
        if (condition instanceof LaxCondition) {
            renameVar(((LaxCondition) condition).getGraph(), o, n);
        } else if (condition instanceof OperatorCondition) {
            renameVar((OperatorCondition) condition, o, n);
        } else {
            assert false;
        }
    }

    /**
     * Call renameVar for both expr1 and expr2
     */
    private void renameVar(OperatorCondition opCon, String o, String n) {
        renameVar(opCon.getExpr1(), o, n);
        renameVar(opCon.getExpr2(), o, n);
    }

    /**
     * Given a graph, an old graph node name and a new graph node name
     * Check if the old graph node name exists in the current graph according to the graphNodeMap
     * If so replace the old graph node name with the new graph node name
     * @param graph     The graph
     * @param o         The old graph node name
     * @param n         The new graph node name
     */
    public void renameVar(PlainGraph graph, String o, String n) {
        PlainNode node = graphNodeMap.get(graph).get(o);
        if (node != null) {
            // if the graph contains the old node, replace that name with the new name
            if (!graphNodeMap.get(graph).containsKey(n)) {
                graphNodeMap.get(graph).remove(o);
                graphNodeMap.get(graph).put(n, node);
            } else {
                // the new node is already part of the graph, therefore move all the edges of the old node to the new node if they do not exist yet.
                PlainNode oldNode = graphNodeMap.get(graph).get(o);

                // handle the incoming edges
                for (PlainEdge in: new HashSet<>(graph.inEdgeSet(oldNode))) {
                    addEdge(graph, getVarName(graph, in.source()), in.label().text(), n);
                    removeEdge(graph, in);
                }

                //handle the outgoing edges
                for (PlainEdge out: new HashSet<>(graph.outEdgeSet(oldNode))) {
                    if (out.source() != out.target()) {
                        // self loops are already added by handling the incoming edges
                        addEdge(graph, n, out.label().text(), getVarName(graph, out.target()));
                        removeEdge(graph, out);
                    }
                }

                // remove the old node
                removeNode(graph, oldNode);
            }
        }
    }

    /**
     * Given a PlainNode from the graph, return its variable name;
     */
    public String getVarName(PlainGraph graph, PlainNode grooveName) {
        return graphNodeMap.get(graph).entrySet().stream()
                .filter(e -> e.getValue().equals(grooveName))
                .collect(Collectors.toList())
                .get(0)
                .getKey();
    }

    /**
     * Used to get the name of the variable of a variableGraph (v:T)
     * In which the variable should be placed at the node with nodenumber 0
     * @param graph     The variable graph, which may contain multiple nodes
     * @return          The name of the node with nodenumber 0
     */
    public String getVarNameOfNoden0(PlainGraph graph) {
        List<String> varNames = graphNodeMap.get(graph).entrySet()
                .stream()
                .filter(e -> e.getValue().getNumber() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        assert  varNames.size() == 1;
        return varNames.get(0);
    }

    /**
     * Given a variable Name (exists only in the code), determine its type by looking in the OutEdge set of the given variable name
     * @param key       The variable name
     * @return          The type of the variable
     *                  or null if the variable does not exists
     */
    public String getVariableType(String key) {
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
    public PlainGraph cloneGraph(PlainGraph graph) {
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
    public Map<String, PlainNode> cloneNodeMap(PlainGraph graph) {
        return graphNodeMap.get(graph).entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Given a Condition, create a clone
     */
    public Condition cloneCondition(Condition con) {
        if (con instanceof OperatorCondition) {
            // clone both conditions
            Condition expr1 = cloneCondition(((OperatorCondition) con).getExpr1());
            Condition expr2 = cloneCondition(((OperatorCondition) con).getExpr2());

            // determine which kind of OperatorCondition it was
            if (con instanceof AndCondition) {
                return new AndCondition(expr1, expr2);
            } else if (con instanceof ImpliesCondition) {
                return new ImpliesCondition(expr1, expr2);
            } else if (con instanceof OrCondition) {
                return new OrCondition(expr1, expr2);
            }
        } else if (con instanceof LaxCondition) {
            PlainGraph graph = cloneGraph(((LaxCondition) con).getGraph());
            Condition condition = null;
            if (((LaxCondition) con).getCondition() != null) {
                // if the laxcondition has a condition clone the condition
                condition = cloneCondition(((LaxCondition) con).getCondition());
            }
            return new LaxCondition(((LaxCondition) con).getQuantifier(), graph, condition);
        }
        assert false;   // shouldn't happen
        return null;
    }

    /**
     * Given a graph in which v:T is already defined add the attributed graph components
     * (rule15)
     */
    public void addAttributedGraph(PlainGraph graph, String varName, Tuple2<String, TypeNode> attrType, Operator op, Constant n) {
        String aType = attrType.getSecond().text();
        String attr = addNode(graph, aType);

        // connect attribute to the v:T
        addEdge(graph, varName, attrType.getFirst(), attr);

        // create the constant node
        String attr2 = addNode(graph, n.getGrooveString());

        // create production nodes
        addProductionRule(graph, attr, aType, op, attr2);
    }

    /**
     * Create the production component according to GROOVE notation
     */
    public void addProductionRule(PlainGraph graph, String attr1, String attrType, Operator op, String attr2) {
        // create production nodes
        String operator = op.getGrooveString(attrType);
        String prod = addNode(graph, String.format("%s:", PROD));
        String bool = addNode(graph, BooleanConstant.TRUE.getGrooveString());

        //connect production nodes
        addEdge(graph, prod, "arg:0", attr1);
        addEdge(graph, prod, "arg:1", attr2);
        addEdge(graph, prod, operator, bool);
    }

    /**
     * Given an empty graph, create a Node with its Attribute,
     * given the double tuple which contains <code><<String, TypeNode>, <String, TypeNode>>></code>
     * And creates 2 nodes, given the 2 typeNodes as type and the association given the name in the second String
     *
     * @return The name of the created attribute node.
     */
    public String addNodeWithAttribute(PlainGraph graph, Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> exprAttrType) {
        String varName = addNode(graph, exprAttrType.getFirst().getSecond().text());
        String attrName = addNode(graph, exprAttrType.getSecond().getSecond().text());
        addEdge(graph, varName, exprAttrType.getSecond().getFirst(), attrName);
        return attrName;
    }

    /**
     * Negates a graph such that:
     *  If there exists a production rule, replace the TRUE boolean for FALSE and vice versa
     *  Else Negate the type edges
     *      If there exists a not edge already, remove it
     *      Else add the not edge
     */
    public void negate(PlainGraph graph) {
        if (graph.edgeSet().stream().anyMatch(e -> e.label().text().equals(String.format("%s:", PROD)))){
            // we are talking about negating a comparison if there is a prod rule:
            // therefore: replace all TRUE boolean for FALSE and vice versa.
            List<? extends PlainEdge> bools = graph.edgeSet().stream().filter(e ->
                    e.label().text().equals(String.format("%s:%s", BOOL, TRUE_STRING)) ||
                            e.label().text().equals(String.format("%s:%s", BOOL, FALSE_STRING))
            ).collect(Collectors.toList());

            for (PlainEdge bool : bools) {
                if (bool.label().text().contains(TRUE_STRING)) {
                    addEdge(graph, bool.source(), String.format("%s:%s", BOOL, FALSE_STRING), bool.target());
                } else {
                    addEdge(graph, bool.source(), String.format("%s:%s", BOOL, TRUE_STRING), bool.target());
                }
                removeEdge(graph, bool);
            }
        } else {
            // if we are talking about negating an edge:
            for (PlainEdge edge : graph.edgeSet()) {
                if (labelContainsType(edge.label().text())) {
                    // if there exists a type edge check whether the not edge exists already;
                    List<PlainEdge> notEdge = getNotEdge(graph, edge);
                    if (notEdge.isEmpty()) {
                        // add not edge
                        addEdge(graph, edge.source(), String.format("%s:", NOT), edge.target());
                    } else {
                        // remove not edge
                        removeEdge(graph, notEdge.get(0));
                    }
                }
            }
        }
    }

    /**
     * Returns the corresponding "NOT:" edge of the given edge in the given graph,
     * if possible, otherwise an empty list
     */
    private List<PlainEdge> getNotEdge(PlainGraph graph, PlainEdge edge) {
        return graph.edgeSet().stream().filter(e ->
                e.source().equals(edge.source())
                        && e.target().equals(edge.target())
                        && e.label().text().equals(String.format("%s:", NOT)))
                .collect(Collectors.toList());
    }

    private boolean labelContainsType(String label) {
        return label.startsWith(String.format("%s:", TYPE));
    }

    /**
     * Given a LaxCondition merge the recursive laxConditions into one graph including the quantification
     * @param c     The LaxCondition
     * @return      The resulting graph
     */
    public PlainGraph laxToGraph(LaxCondition c) {
        validate(c, new HashSet<>());
        return laxToGraph(c.getGraph(), c, 0, null);
    }

    /**
     * Because we have to clone the graphs for conditions like the if else then, it is possible that there
     * are different nodes with the same name, so we have to validate the Condition and rename the variables
     * If we can find such a name collision
     */
    private Set<String> validate(Condition c, Set<String> vars) {
        if (c instanceof LaxCondition) {
            vars.addAll(graphNodeMap.get(((LaxCondition) c).getGraph()).keySet());
            if (((LaxCondition) c).getCondition() != null) {
                Set<String> val = validate(((LaxCondition) c).getCondition(), new HashSet<>(vars));
                vars.addAll(val);
            }
            return vars;
        } else if (c instanceof OperatorCondition){
            Set<String> val1 = validate(((OperatorCondition) c).getExpr1(), new HashSet<>(vars));
            Set<String> val2 = validate(((OperatorCondition) c).getExpr2(), new HashSet<>(vars));
            Set<String> intersection = intersection(val1, val2);
            intersection.removeAll(vars);
            if (!intersection.isEmpty()) {
                // there are nodes that exist in both expr1 and expr2 but not in its parents;
                // so rename those nodes in expr2 such that the laxCondition can be transformed to one graph
                for (String i : intersection) {
                    renameVar(((OperatorCondition) c).getExpr2(), i, getUniqueNodeName());
                }
                // reset val2 since node names are renamed
                val2 = validate(((OperatorCondition) c).getExpr2(), new HashSet<>(vars));
            }
            vars.addAll(val1);
            vars.addAll(val2);
            return vars;
        } else {
            assert false; //shouldn't happen
        }
        return vars;
    }

    /**
     * Given 2 sets return the intersection
     */
    private Set<String> intersection(Set<String> s1, Set<String> s2) {
        Set<String> intersection = new HashSet<>(s1);
        intersection.retainAll(s2);
        return intersection;
    }

    private PlainGraph laxToGraph(PlainGraph graph, LaxCondition c, int level, String prevQuant) {
        // contains the nodeMap of the previous quantLevel
        Map<String, PlainNode> nodeMap = new HashMap<>();
        // add the graph to the existing graph
        if (level > 0 ) {
            nodeMap = cloneNodeMap(graph);
            graph = mergeGraphs(graph, c.getGraph());
        }

        // create the quantification of this laxCondition
        String quantLvl = addNode(graph, c.getQuantifier().getGrooveString());

        // connect the nodes of the graph with its quantifier
        for (Map.Entry<String, PlainNode> entry : graphNodeMap.get(c.getGraph()).entrySet()) {
            if (!entry.getKey().equals(quantLvl) && !nodeMap.containsKey(entry.getKey())) {
                //the quantifier shouldn't connect with itself and check if the node didn't exist already in the previous quantlevel
                addEdge(graph, entry.getKey(), AT, quantLvl);
            }
        }

        PlainNode quantifier = graphNodeMap.get(graph).get(quantLvl);
        if (graph.inEdgeSet(quantifier).size() == 1) {
            // if no node is connected with the quantifier, we can remove the quantifier
            // the one incoming edge is the self loop which contains the label of the quantifier
            removeNode(graph, quantifier);
        } else if (level > 0) {
            // create connection between the current quantification level and the previous quantification level
            addEdge(graph, quantLvl, IN, prevQuant);
        }

        // all nodes from this level are created and connected. Start with the next level if applicable
        if (c.getCondition() != null) {
            return laxToGraphCondition(graph, c.getCondition(), level + 1, quantLvl);
        } else {
            return graph;
        }
    }

    /**
     * For an OperatorCondition both conditions should be handled
     */
    private PlainGraph laxToGraph(PlainGraph graph, OperatorCondition c, int level, String prevQuant) {
        laxToGraphCondition(graph, c.getExpr1(), level, prevQuant);
        laxToGraphCondition(graph, c.getExpr2(), level, prevQuant);
        return graph;
    }

    /**
     * Determine if the Condition is an LaxCondition or an AndCondition
     * Cast the condition to the right type and call the right method
     */
    private PlainGraph laxToGraphCondition(PlainGraph graph, Condition c, int level, String prevQuant) {
        if (c instanceof LaxCondition) {
            return laxToGraph(graph, (LaxCondition) c, level, prevQuant);
        } else if (c instanceof OperatorCondition) {
            return laxToGraph(graph, (OperatorCondition) c, level, prevQuant);
        }
        assert false; // shouldn't happen
        return null;
    }

    /**
     * check if g1 subset g2 or g2 subset g1
     * only in the first case g1 should be replaced with g2
     * else glue the two graphs together
     */
    public PlainGraph mergeGraphs(PlainGraph g1, PlainGraph g2){
        // check if g1 subset g2 or g2 subset g1
        // only in the first case g1 should be replaced with g2
        // else glue the two graphs together
        if (subsetGraphs(g1, g2)) {
            // make sure that the name does not change, in the root case of the tree the g1 contains the name of the invariant
            g2.setName(g1.getName());
            return g2;
        } else if (!subsetGraphs(g2, g1)) {
            return mergeGraphsInternal(g1, g2);
        }
        return g1;
    }

    /**
     * Given two graphs merge the second graph <code>g2</code> into the first graph <code>g1</code>
     * By adding all nodes of g2, not existing in g1 to g1
     * and connect all edges existing in g2, not existing in g1 to g1
     * @param g1    The first graph
     * @param g2    The second graph
     */
    public PlainGraph mergeGraphsInternal(PlainGraph g1, PlainGraph g2) {
        // Create all nodes of g2 in g1
        // AddNode does check if the node does exist already, if so it doesn't create a new one
        for (Map.Entry<String, PlainNode> entry: graphNodeMap.get(g2).entrySet()){
            addNode(g1, entry.getKey(), null);
        }

        // create all edges of g2 in g1
        for (PlainEdge edge: g2.edgeSet()) {
            // check if the edge exists in g1, if so check if the variable names of the source and the target are also the same and not just the node names
            // Since the node names are numbered in each graph starting with 0, collisions could exists without the 2nd and 3rd check
            // Inverse the whole such that if this edge doesn't exist create it in g1
            if (! (g1.containsEdge(edge) && getVarName(g2, edge.source()).equals(getVarName(g1, edge.source())) && getVarName(g2, edge.target()).equals(getVarName(g1, edge.target())))) {
                addEdge(g1, getVarName(g2, edge.source()), edge.label().text(), getVarName(g2, edge.target()));
            }
        }
        return g1;
    }

    /**
     * Determines if g1 is a subset of g2
     * @param g1    The first graph
     * @param g2    The second graph
     * @return      True if g1 is a subset of g2
     *              False else
     */
    public boolean subsetGraphs(PlainGraph g1, PlainGraph g2) {
        for (PlainNode node : g1.nodeSet()) {
            // check if all nodes of g1 also exist in g2
            String nodeName = getVarName(g1, node);
            if (!graphNodeMap.get(g2).containsKey(nodeName)) {
                return false;
            }
        }

        for (PlainEdge edge: g1.edgeSet()) {
            // check if all edges of g1 also exist in g2
            // and check if the edge has the same negation
            if (!(g2.containsEdge(edge) && getNotEdge(g1, edge).size() == getNotEdge(g2, edge).size())) {
                return false;
            }
        }
        return true;
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
    private String getUniqueNodeName() {
        return String.format("N%d", uniqueNode++);
    }

    /**
     * An helper function that connects the Groove names within one specific graph with the variable names (and generated names)
     * Of the different Lax conditions such that not every graph starts with a node n0
     */
    public String graphToString(PlainGraph graph) {
        String result = graph.edgeSet().toString();
        for (Map.Entry<String, PlainNode> v : graphNodeMap.get(graph).entrySet()) {
            result = result.replace(v.getValue().toString(),  v.getKey());
        }
        return result;
    }

    /**
     * A method that makes sure that the correct conToString method is called
     *
     * This method is responsible for the Condition toString method, overriding the default toString was not possible
     * because of the connection with the GraphBuilder
     */
    public String conToString(Condition condition) {
        if (condition instanceof LaxCondition) {
            return conToString((LaxCondition) condition);
        } else if (condition instanceof AndCondition) {
            return conToString((AndCondition) condition);
        } else if (condition instanceof OrCondition) {
            return conToString((OrCondition) condition);
        } else if (condition instanceof ImpliesCondition) {
            return conToString((ImpliesCondition) condition);
        }
        //shouldn't happen
        assert false;
        return null;
    }

    private String conToString(LaxCondition laxCon) {
        String g = graphToString(laxCon.getGraph());
        if (laxCon.getCondition() == null) {
            return String.format("%s(%s)", laxCon.getQuantifier(), g);
        } else {
            return String.format("%s(%s, %s)", laxCon.getQuantifier(), g, conToString(laxCon.getCondition()));
        }
    }

    private String conToString(AndCondition andCon) {
        return String.format("%s \u2227 %s", conToString(andCon.getExpr1()), conToString(andCon.getExpr2()));
    }

    private String conToString(OrCondition orCon) {
        return String.format("%s \u2228 %s", conToString(orCon.getExpr1()), conToString(orCon.getExpr2()));
    }

    private String conToString(ImpliesCondition implCon) {
        return String.format("%s \u2192 %s", conToString(implCon.getExpr1()), conToString(implCon.getExpr2()));
    }
}
