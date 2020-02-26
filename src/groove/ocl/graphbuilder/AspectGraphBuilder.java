package groove.ocl.graphbuilder;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.graph.GraphRole;
import groove.ocl.GrammarStorage;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.lax.graph.AttributedGraph;
import groove.ocl.lax.graph.Graph;
import groove.ocl.lax.graph.Variable;
import groove.ocl.lax.graph.VariableFactory;
import groove.ocl.lax.graph.constants.BooleanConstant;
import groove.ocl.lax.graph.constants.Constant;
import groove.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AspectGraphBuilder {
    private final static Logger LOGGER = Log.getLogger(AspectGraphBuilder.class.getName());

    private final static String AT = "@";
    private final static String PROD = "prod";
    private final static String IN = "in";

    private AspectGraph graph;
    private int nodeNumber;
    private Map<String, AspectNode> nodeMap;

    /**
     * Creates or overwrites the graph with the name {@param ruleName}
     * And provides helper methods to make it easier to create graphs of type Rule conditions.
     */
    public AspectGraphBuilder(String graphName) {
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
        if (!nodeMap.containsKey(name)) {
            AspectNode node = new AspectNode(nodeNumber++, GraphRole.RULE);
            graph.addNode(node);
            addEdge(node, label, node);
            nodeMap.put(name, node);
        }
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
     * Given a LaxCondition generate the corresponding graph in Groove
     */
    public void laxToGraph(LaxCondition laxCon) {
        laxToGraph(laxCon, 0);
    }

    private void laxToGraph(LaxCondition laxCon, int level){
        String quantLvl = Integer.toString(level);
        addNode(quantLvl, laxCon.getQuantifier().getGrooveString());

        // Create connection between the current quantification level and the previous quantification level
        if (level > 0) {
            addEdge(quantLvl, IN, Integer.toString(level-1));
        }

        Graph expr = laxCon.getGraph();
        if (expr instanceof Variable) {
            Variable var = (Variable) expr;
            // create the variable and connect it with the current quantification level
            createVariableNode(var);
            addEdge(var.getVariableName(), AT, quantLvl);
        } else if (expr instanceof AttributedGraph) {
            createAttributedGraph((AttributedGraph) expr, quantLvl);
        }

        // all nodes from this level are created and connected. Start with the next level if applicable
        if (laxCon.getCondition() instanceof LaxCondition) {
            laxToGraph((LaxCondition) laxCon.getCondition(), level + 1);
        }
    }

    /**
     * Given an Attributed Graph create the corresponding Groove graph in the correct quantification level
     * @param aGraph        The attributed graph
     * @param quantLvl      The correct quantification level
     */
    private void createAttributedGraph(AttributedGraph aGraph, String quantLvl) {
        // if the variable does not exist yet, create it; in most cases it will exist
        if (!VariableFactory.contains(aGraph.getVariable().getVariableName())) {
            createVariableNode(aGraph.getVariable());
            addEdge(quantLvl, AT, aGraph.getVariable().getVariableName());
        }

        // create the path from the variable to the attribute and the connection with the quantification
        createVariableNode(aGraph.getAttr1());
        addEdge(aGraph.getVariable().getVariableName(), aGraph.getAttr1().getVariableName(), aGraph.getAttr1().getVariableName());
        addEdge(aGraph.getAttr1().getVariableName(), AT, quantLvl);

        // create the second attribute to compare with
        //TODO: what if attr2 is not a constant
        String attr2 = "";
        if (aGraph.getAttr2() instanceof Constant) {
            attr2 = createConstant((Constant) aGraph.getAttr2());
        }

        // create the production rule which compares the two attributes
        String prod = prodLevelString(quantLvl);
        String operator = aGraph.getOperator().getGrooveString(aGraph.getAttr1().getClassName());
        addNode(prod, String.format("%s:", PROD));
        addNode(BooleanConstant.TRUE.getGrooveString(), BooleanConstant.TRUE.getGrooveString());

        addEdge(prod, AT, quantLvl);
        addEdge(prod, "arg:0", aGraph.getAttr1().getVariableName());
        addEdge(prod, "arg:1", attr2);
        addEdge(prod, operator, BooleanConstant.TRUE.getGrooveString());
    }

    /**
     * Given a variable create the corresponding node
     */
    private void createVariableNode(Variable variable){
        String name = variable.getVariableName();
        String typeString = String.format("type:%s", variable.getClassName());
        addNode(name, typeString);
    }

    /**
     * Given a constant create the corresponding node in Groove
     * @return the label of the created node.
     */
    private String createConstant(Constant constant) {
        String constantString = constant.getGrooveString();
        addNode(constantString, constantString);
        return constantString;
    }

    /**
     * Helper function that generates the production label for a given quantification level
     */
    private String prodLevelString(String level) {
        return String.format("%s%s", PROD, level);
    }

    /**
     * Saves the created graph
     */
    public void save() {
        GrammarStorage.saveGraph(graph);
    }
}
