package groove.ocl.parser;

import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.node.*;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.graph.plain.PlainGraph;
import groove.ocl.GrammarStorage;
import groove.ocl.InvalidOCLException;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.Operator;
import groove.ocl.lax.Quantifier;
import groove.ocl.lax.condition.AndCondition;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.lax.graph.constants.BooleanConstant;
import groove.ocl.lax.graph.constants.Constant;
import groove.ocl.lax.graph.constants.IntConstant;
import groove.ocl.lax.graph.constants.StringConstant;
import groove.util.Log;
import groovy.lang.Tuple2;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static groove.ocl.Groove.EQ;

/**
 * recursive tree visitor that uses the {@link de.tuberlin.cs.cis.ocl.parser.analysis.AnalysisAdapter#getOut(Node)} for the recursive return values
 * Therefore the defaultOut method is overriden such that it will give through the value to its parent.
 * In this way the parent can determine if it should be changed or that it also gives the value through.
 */
public class TranslateOCLToLax extends DepthFirstAdapter {

    private final static Logger LOGGER = Log.getLogger(TranslateOCLToLax.class.getName());

    // The defined type graphs
    private TypeGraph typeGraph;

    private LaxCondition result;

    public TranslateOCLToLax() {
        this.typeGraph = GrammarStorage.getTypeGraphs();
    }

    @Override
    public void outStart(Start node) {
        result = (LaxCondition) getOut(node);
    }

    @Override
    public void outAConstraint(AConstraint node) {
        PlainGraph graph = (PlainGraph) getOut(node.getContextDeclaration());
        LaxCondition con = (LaxCondition) getOut(node.getContextBodypart().get(0));
        resetOut(node, new LaxCondition(Quantifier.FORALL, graph, con));
    }

    /**
     * The type of the classifier will be set in types
     * The variable name will be given through via this.out
     */
    @Override
    public void outAClassifierContextKind(AClassifierContextKind node) {
        String clazz, var;
        // the type will be set in types
        if (node.getClassifierType() != null) {
            // Equals to rule2
            // the context class is given a variable (a:Person)
            clazz = getOut(node.getClassifierType()).toString();
            var = getOut(node.getName()).toString();
        } else {
            // Equals to rule1
            // the context class is given the variable self (self:Person)
            clazz = getOut(node.getName()).toString();
            var = "self";
        }

        PlainGraph graph = GraphBuilder.createGraph();
        GraphBuilder.addNode(graph, var, clazz);
        resetOut(node, graph);
    }

    @Override
    public void outARelationalExpression(ARelationalExpression node) {
        // operators are {=, <>}
        // rules that could apply are 15,16,17,18
        defaultOut(node);
    }

    @Override
    public void outACompareableExpression(ACompareableExpression node) {
        // operators are {<, <=, =>, >}
        // rules that could apply are 17,18
        if (node.getComparison() != null){
            String expr1 = getOut(node.getAdditiveExpression()).toString();
            Object expr2 = getOut(((AComparison) node.getComparison()).getAdditiveExpression());
            Operator op = (Operator) getOut(((AComparison) node.getComparison()).getCompareOperator());

            Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType = determineTypeAndAttribute(expr1);
            expr1 = expr1AttrType.getFirst().getFirst();
            if (expr2 instanceof Constant) {
                // so its rule17
                PlainGraph var = GraphBuilder.createGraph();
                String varName = GraphBuilder.addNode(var, expr1AttrType.getFirst().getSecond().text());

                LaxCondition trn = tr_N(expr1, GraphBuilder.cloneGraph(var));

                PlainGraph attrGraph = GraphBuilder.cloneGraph(var);
                GraphBuilder.addAttributedGraph(attrGraph, varName, expr1AttrType.getSecond(),op ,(Constant) expr2);

                // given the values create the right LaxCondition
                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, new AndCondition(trn, new LaxCondition(Quantifier.EXISTS, attrGraph))));
            } else {
                // its rule 18
                Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr2AttrType = determineTypeAndAttribute(expr2.toString());
            }
        }
    }

    @Override
    public void outAPostfixExpression(APostfixExpression node) {
        if (getOut(node) instanceof Constant) {
            super.outAPostfixExpression(node);
        } else {
            resetOut(node);
        }
    }

    @Override
    public void outALtCompareOperator(ALtCompareOperator node) {
        resetOut(node, Operator.LT);
    }

    @Override
    public void outALteqCompareOperator(ALteqCompareOperator node) {
        resetOut(node, Operator.LTEQ);
    }

    @Override
    public void outAGtCompareOperator(AGtCompareOperator node) {
        resetOut(node, Operator.GT);
    }

    @Override
    public void outAGteqCompareOperator(AGteqCompareOperator node) {
        resetOut(node, Operator.GTEQ);
    }

    @Override
    public void outAName(AName node) {
        resetOut(node);
    }

    @Override
    public void outANumberLiteral(ANumberLiteral node) {
        resetOut(node, new IntConstant(Integer.parseInt(node.getNumberLiteral().getText())));
    }

    @Override
    public void outAStringLiteral(AStringLiteral node) {
        resetOut(node, new StringConstant(node.getStringLiteral().getText()));
    }

    @Override
    public void outABooleanLiteral(ABooleanLiteral node) {
        Constant c = Boolean.parseBoolean(node.getBooleanLiteral().getText()) ? BooleanConstant.TRUE : BooleanConstant.FALSE;
        resetOut(node, c);
    }

    @Override
    public void defaultOut(Node node) {
        setOut(node.parent(), getOut(node));
    }

    private void resetOut(Node node) {
        // for some reason the parser adds a lot of spaces, which have to be removed
        resetOut(node, node.toString().replaceAll(" ", ""));
    }

    /**
     * Helper method that resets the current node and
     * sets the parent value such that it is possible to give that value through if necessary
     */
    private void resetOut(Node node, Object o) {
        setOut(node , o);
        setOut(node.parent(), o);
    }

    /**
     * Given an expression determine the new expr and attribute (expr.attr)
     * and the type of both the new expr and the attr
     *
     * e.g. "self.age" in the context of Person returns ((self, Person)(age, Int))
     * @param expr The expression
     * @return      A double Tuple with the expression without the attribute and its TypeNode
     *              and the extracted attribute and its TypeNode.
     */
    private Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> determineTypeAndAttribute(String expr) {
        String[] split = expr.split("\\.");

        String attr = split[split.length-1];
        TypeNode typeAttr = determineType(expr);
        Tuple2<String, TypeNode> attrType = new Tuple2<>(attr, typeAttr);

        expr = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));
        TypeNode type = determineType(expr);
        Tuple2<String, TypeNode> exprType = new Tuple2<>(expr, type);

        return new Tuple2<>(exprType, attrType);
    }

    /**
     * Given a navigation expression (e.g. self.age) get the corresponding TypeNode
     * @param expr      A navigation expression String, separated by dots
     * @return          The corresponding TypeNode from the TypeGraph
     */
    private TypeNode determineType(String expr) {
        List<String> split = new ArrayList<>();
        Collections.addAll(split, expr.split("\\."));

        // TODO check if an expression could start without a custom variable (e.g. self), if so a nullpointer exists here
        String curType = GraphBuilder.getVariableType(split.get(0));
        split.remove(split.get(0));

        // find the first node
        List<TypeNode> types = typeGraph.nodeSet().stream()
                .filter(n -> n.text().equals(curType))
                .collect(Collectors.toList());

        if (types.isEmpty()) {
            LOGGER.severe(String.format("In the type graph is no %s-type defined", curType));
            throw new InvalidOCLException();
        }

        TypeNode type = types.get(0);
        for (String i: split) {
            // follow the edges to the final type node
            List<? extends TypeEdge> typeEdges = typeGraph.outEdgeSet(type).stream()
                    .filter(e -> e.text().equals(i))
                    .collect(Collectors.toList());

            if (typeEdges.isEmpty()){
                // if the edge does not exist in the type graph, the given OCL expression is not correct
                LOGGER.severe(String.format("The outgoing edge %s does not exist for class %s", i, type));
                throw new InvalidOCLException();
            }
            type = typeEdges
                    .get(0)
                    .target();
        }

        return type;
    }

    /**
     * The tr_N translation rules
     */
    private LaxCondition tr_N(String expr, PlainGraph graph) {
        LaxCondition con;
        if (expr.contains(".")) {
            //TODO What if the expr contains multiple dots?
            String[] split = expr.split("\\.");

            String role = split[1];
            TypeNode roleType = determineType(expr);

            expr = split[0];
            TypeNode exprType = determineType(expr);

            //TODO figure out which side the clan arrows go
            if (exprType.getSubtypes().contains(roleType)) {
                // rule24
                con = null;
            } else {
                // rule23
                PlainGraph varPrime = GraphBuilder.createGraph();
                String vp = GraphBuilder.addNode(varPrime, exprType.text());
                LaxCondition trn = tr_N(expr, varPrime);

                GraphBuilder.addNode(graph, vp, exprType.text());
                GraphBuilder.addEdge(graph, vp, role, GraphBuilder.getVarName(graph));

                con = new LaxCondition(Quantifier.EXISTS, graph, trn);
            }
        } else {
            // rule22
            String vp = GraphBuilder.getVarName(graph);
            GraphBuilder.addNode(graph, expr, GraphBuilder.getVariableType(vp));
            GraphBuilder.addEdge(graph, vp, EQ, expr);

            con = new LaxCondition(Quantifier.EXISTS, graph);
        }
        return con;
    }

    public LaxCondition getResult() {
        return result;
    }
}
