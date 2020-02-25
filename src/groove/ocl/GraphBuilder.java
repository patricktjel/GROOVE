package groove.ocl;

import groove.grammar.aspect.AspectGraph;
import groove.grammar.aspect.AspectLabel;
import groove.grammar.aspect.AspectNode;
import groove.graph.GraphRole;
import groove.ocl.lax.*;
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

    public void laxToGraph(LaxCondition laxCon) {
        laxToGraph(laxCon, 0);
    }

    // TODO: there is no difference in multiple levels of existance/universal quantification
    private void laxToGraph(LaxCondition laxCon, int level){
        String quantLvl = Integer.toString(level);
        if (laxCon.getQuantifier().equals(Quantifier.FORALL)) {
            addNode(quantLvl, "forall:");
        } else { // Quantifier.Exists
            addNode(quantLvl, "exists:");
        }

        // Create connection between the current quantification level and the previous quantification level
        if (level > 0) {
            addEdge(quantLvl, "in", Integer.toString(level-1));
        }

        Expression expr = laxCon.getExpression();
        if (expr instanceof Variable) {
            //TODO: make a connection with the previous quantification level
            Variable var = (Variable) expr;
            // create the variable and connect it with the current quantification level
            createVariableNode(var);
            addEdge(var.getVariableName(), "@", quantLvl);
        } else if (expr instanceof AttributedGraph) {
            AttributedGraph aGraph = (AttributedGraph) expr;
            String prod = prodLevelString(quantLvl);

            createVariableNode(aGraph.getVariable());

            addNode(prod, "prod:");
            addNode("bool:true", "bool:true");

            addEdge(prod, aGraph.getOperator().name(), "bool:true");
        }

        // all nodes from this level are created and connected. Start with the next level if applicable
        if (laxCon.getCondition() instanceof LaxCondition) {
            laxToGraph((LaxCondition) laxCon.getCondition(), level + 1);
        }
    }

    /**
     * Given a variable create the corresponding node
     * @param variable
     */
    private void createVariableNode(Variable variable){
        String name = variable.getVariableName();
        String typeString = String.format("type:%s", variable.getClassName());
        addNode(name, typeString);
    }

    /**
     * Helper function that generates the production label for a given quantification level
     */
    private String prodLevelString(String level) {
        return String.format("prod%s", level);
    }

    /**
     * Saves the created graph
     */
    public void save() {
        GrammarStorage.saveGraph(graph);
    }
}
