package groove.ocl.parser;

import com.sun.deploy.util.StringUtils;
import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.node.*;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.graph.plain.PlainGraph;
import groove.ocl.InvalidOCLException;
import groove.ocl.OCL;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.Operator;
import groove.ocl.lax.Quantifier;
import groove.ocl.lax.condition.*;
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
    
    private GraphBuilder graphBuilder;

    private Map<LaxCondition, GraphBuilder> results;

    public TranslateOCLToLax(TypeGraph typeGraph) {
        this.typeGraph = typeGraph;
        this.results = new HashMap<>();
        this.graphBuilder = new GraphBuilder();
    }

    @Override
    public void outStart(Start node) {
        // nothing yet, but the start node doesn't have a parent
    }

    @Override
    public void outAConstraint(AConstraint node) {
        PlainGraph contextGraph = (PlainGraph) getOut(node.getContextDeclaration());

        // Every contextDeclaration may have multiple contextBodyParts, each stands for an invariant
        for (PContextBodypart inv: node.getContextBodypart()) {
            PlainGraph graph = graphBuilder.cloneGraph(contextGraph);
            Condition con = (Condition) getOut(inv);
            LaxCondition result = new LaxCondition(Quantifier.FORALL, graph, con);

            // if the invariant has a name, give this graph that name
            PName invName = ((AConstraintContextBodypart) inv).getName();
            if (invName != null){
                result.getGraph().setName(invName.toString().trim());
            }

            // save the LaxCondition together with its graphbuilder such that the connection between the label names and node names isn't lost
            results.put(result, graphBuilder);
        }
        // be ready for a new constraint
        this.graphBuilder = new GraphBuilder();
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

        PlainGraph graph = graphBuilder.createGraph();
        graphBuilder.addNode(graph, var, clazz);
        resetOut(node, graph);
    }

    @Override
    public void outARelationalExpression(ARelationalExpression node) {
        // operators are {=, <>}
        // rules that could apply are 15,16,17,18 (Research topics)
        defaultOut(node);
    }

    @Override
    public void outALogicalExpression(ALogicalExpression node) {
        if (!node.getImplication().isEmpty()) {
            Condition con1 = (Condition) getOut(node.getBooleanExpression());

            for (PImplication impNode : node.getImplication()) {
                Condition con2 = (Condition) getOut(impNode);
                // PImplication can only be an implies
                con1 = new ImpliesCondition(con1, con2);
            }
            resetOut(node, con1);
        } else {
            defaultOut(node);
        }
    }

    @Override
    public void outABooleanExpression(ABooleanExpression node) {
        if (!node.getBooleanOperation().isEmpty()) {
            Condition con1 = (Condition) getOut(node.getRelationalExpression());

            // loop through all the operations and create a nested condition in con1
            for (PBooleanOperation nodeOp : node.getBooleanOperation()) {
                Condition con2 = (Condition) getOut(nodeOp);

                // op can be AAnd, AOr, AXor
                PBooleanOperator op = ((ABooleanOperation) nodeOp).getBooleanOperator();
                if (op instanceof AAndBooleanOperator) {
                    // rule5
                    con1 = new AndCondition(con1, con2);
                } else if (op instanceof AOrBooleanOperator) {
                    // rule6
                    con1 = new OrCondition(con1, con2);
                } else {
                    // makes sure that we do not miss one of the (new) operations
                    assert false;
                }
            }
            resetOut(node, con1);
        } else {
          defaultOut(node);
        }
    }

    @Override
    public void outACompareableExpression(ACompareableExpression node) {
        // operators are {<, <=, =>, >}
        // rules that could apply are 15, 16, 17
        if (node.getComparison() != null){
            String expr1 = getOut(node.getAdditiveExpression()).toString();
            Object expr2 = getOut(((AComparison) node.getComparison()).getAdditiveExpression());
            Operator op = (Operator) getOut(((AComparison) node.getComparison()).getCompareOperator());

            Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType = determineTypeAndAttribute(expr1);
            expr1 = expr1AttrType.getFirst().getFirst();
            if (expr2 instanceof Constant) {
                // so its rule15
                PlainGraph var = graphBuilder.createGraph();
                String varName = graphBuilder.addNode(var, expr1AttrType.getFirst().getSecond().text());

                LaxCondition trn = tr_NS(expr1, graphBuilder.cloneGraph(var));

                PlainGraph attrGraph = graphBuilder.cloneGraph(var);
                graphBuilder.addAttributedGraph(attrGraph, varName, expr1AttrType.getSecond(), op, (Constant) expr2);

                // given the values create the right LaxCondition
                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, new AndCondition(trn, new LaxCondition(Quantifier.EXISTS, attrGraph))));
            } else {
                // its rule16
                Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr2AttrType = determineTypeAndAttribute(expr2.toString());
                expr2 = expr2AttrType.getFirst().getFirst();

                Set<TypeNode> supertypes1 = typeGraph.getSupertypes(expr1AttrType.getFirst().getSecond());
                Set<TypeNode> supertypes2 = typeGraph.getSupertypes(expr2AttrType.getFirst().getSecond());
                if (expr1.equals(expr2)) {
                    // if expr1 == expr 2 only the first part is needed
                    resetOut(node, rule16Part1(expr1AttrType, expr2AttrType, op, expr1, (String) expr2));
                } else if (intersect(supertypes1, supertypes2).isEmpty()) {
                    // if clan(expr1) ∩ clan(expr2) is empty then only the second part is needed
                    resetOut(node, rule16Part2(expr1AttrType, expr2AttrType, op, expr1, (String) expr2));
                } else {
                    // else both parts are necessary
                    LaxCondition p1 = rule16Part1(expr1AttrType, expr2AttrType, op, expr1, (String) expr2);
                    LaxCondition p2 = rule16Part2(expr1AttrType, expr2AttrType, op, expr1, (String) expr2);
                    resetOut(node, new OrCondition(p1, p2));
                }
            }
        } else {
            defaultOut(node);
        }
    }

    /**
     * rule16 (part before the v)
     */
    private LaxCondition rule16Part1(Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType,
                                     Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr2AttrType,
                                     Operator op, String expr1, String expr2) {
        // rule16 (part before the v)
        PlainGraph var = graphBuilder.createGraph();
        String className = graphBuilder.addNode(var, expr1AttrType.getFirst().getSecond().text());
        String attr1Name = graphBuilder.addNode(var, expr1AttrType.getSecond().getSecond().text());
        String attr2Name = graphBuilder.addNode(var, expr2AttrType.getSecond().getSecond().text());
        graphBuilder.addEdge(var, className, expr1AttrType.getSecond().getFirst(), attr1Name);
        graphBuilder.addEdge(var, className, expr2AttrType.getSecond().getFirst(), attr2Name);
        // instead of using a variable x, just connect with attr1Name
        graphBuilder.addProductionRule(var, attr1Name, expr2AttrType.getSecond().getSecond().text(), op, attr2Name);

        PlainGraph attrGraph1 = graphBuilder.cloneGraph(var);
        PlainGraph attrGraph2 = graphBuilder.cloneGraph(var);

        LaxCondition trn1 = tr_NS(expr1, attrGraph1);
        LaxCondition trn2 = tr_NS(expr2, attrGraph2);
        AndCondition andCon = new AndCondition(trn1, trn2);

        return new LaxCondition(Quantifier.EXISTS, var, andCon);
    }

    /**
     * rule16 (part after the v)
     */
    private LaxCondition rule16Part2(Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType,
                                     Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr2AttrType,
                                     Operator op, String expr1, String expr2) {
        PlainGraph var = graphBuilder.createGraph();
        String attr1Name = graphBuilder.addNodeWithAttribute(var, expr1AttrType);

        PlainGraph attrGraph1 = graphBuilder.cloneGraph(var);

        PlainGraph varp = graphBuilder.createGraph();
        String attr2Name = graphBuilder.addNodeWithAttribute(varp, expr2AttrType);

        // instead of using a variable x, just connect with attr1Name
        graphBuilder.addNode(varp, attr1Name, expr1AttrType.getSecond().getSecond().text());
        graphBuilder.addProductionRule(varp, attr1Name, expr2AttrType.getSecond().getSecond().text(), op, attr2Name);

        PlainGraph attrGraph2 = graphBuilder.cloneGraph(varp);

        LaxCondition trn1 = tr_NS(expr1, attrGraph1);
        LaxCondition trn2 = tr_NS(expr2, attrGraph2);
        AndCondition andCon = new AndCondition(trn1, trn2);

        return new LaxCondition(Quantifier.EXISTS, graphBuilder.mergeGraphs(var, varp), andCon);
    }

    private Set<TypeNode> intersect(Set<TypeNode> set1, Set<TypeNode> set2) {
        Set<TypeNode> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    @Override
    public void outAPostfixExpression(APostfixExpression node) {
        if (getOut(node) instanceof Constant) {
            super.outAPostfixExpression(node);
        } else if (!node.getPropertyInvocation().isEmpty()) {
            // if there is an propertyInvocation
            // get the name of the operation and determine which rule to apply
            APropertyCall propertyCall = getAPropertyCAll(node.getPropertyInvocation().getLast());
            assert propertyCall != null;
            String operation = (String) getOut(propertyCall.getPathName());
            String expr1 = (String) getOut(node.getPrimaryExpression());

            @SuppressWarnings("unchecked") // it's a clone so this can't go wrong
            LinkedList<PPropertyInvocation> clone = (LinkedList<PPropertyInvocation>) node.getPropertyInvocation().clone();
            clone.removeLast(); // we want to remove the last therefore we needed the clone
            for (PPropertyInvocation p : clone) {
                expr1 = expr1.concat(String.format(".%s", getOut(p)));
            }

            PlainGraph var = graphBuilder.createGraph();
            graphBuilder.addNode(var, determineType(expr1).text());

            if (OCL.INCLUDES_ALL.equals(operation)){
                String expr2 = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());
                // rule19
                LaxCondition trn1 = tr_NS(expr1, graphBuilder.cloneGraph(var));
                LaxCondition trn2 = tr_NS(expr2, graphBuilder.cloneGraph(var));

                Condition condition = new ImpliesCondition(trn2, trn1);
                resetOut(node, new LaxCondition(Quantifier.FORALL, var, condition));
            } else if (OCL.NOT_EMPTY.equals(operation)) {
                // rule23
                LaxCondition trn = tr_NS(expr1, graphBuilder.cloneGraph(var));
                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, trn));
            } else if (OCL.IS_EMPTY.equals(operation)) {
                // rule24    applying the morgan's law -E(c, L) = A(-c, L)
                LaxCondition trn = tr_NS(expr1, graphBuilder.cloneGraph(var));
                graphBuilder.applyNot(trn.getGraph());
                resetOut(node, new LaxCondition(Quantifier.FORALL, var, trn));
            } else if (OCL.OCL_IS_KIND_OF.equals(operation)) {
                //rule35
                String T = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());
            } else if (OCL.OCL_IS_TYPE_OF.equals(operation)) {
                // rule36
                String T = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());
            } else {
                assert false; //This operation is not implemented
            }
        } else {
            resetOut(node);
        }
    }


    /**
     * returns the _propertyCall_ of PPropertyInvocation because the generated code does not have that getter in the right abstract class
     */
    private APropertyCall getAPropertyCAll(PPropertyInvocation node) {
        if (node instanceof ACollectionPropertyInvocation) {
            return (APropertyCall) ((ACollectionPropertyInvocation) node).getPropertyCall();
        } else if (node instanceof AObjectPropertyInvocation) {
            return (APropertyCall) ((AObjectPropertyInvocation) node).getPropertyCall();
        } else {
            assert false; // shouldn't happen
            return null;
        }
    }

    @Override
    public void outAIfExpression(AIfExpression node) {
        //rule9
        Condition cond = (Condition) getOut(node.getCondition());
        Condition thenBranch = (Condition) getOut(node.getThenBranch());
        Condition elseBranch = (Condition) getOut(node.getElseBranch());

        AndCondition ifThen = new AndCondition(cond, thenBranch);
        AndCondition elseThen = new AndCondition(negate(cond), elseBranch);
        resetOut(node, new OrCondition(ifThen, elseThen));
    }

    /**
     * Create the negation of an Condition
     * @param cond  The condition that gets negated
     * @return      The negated condition
     */
    private Condition negate(Condition cond) {
        if (cond instanceof AndCondition) {
            // -(a ∧ b) = -a v -b
            return new OrCondition(negate(((AndCondition) cond).getExpr1()), negate(((AndCondition) cond).getExpr2()));
        } else if (cond instanceof OrCondition){
            return new AndCondition(negate(((OrCondition) cond).getExpr1()), negate(((OrCondition) cond).getExpr2()));
        } else if (cond instanceof ImpliesCondition) {
            return new AndCondition(((ImpliesCondition) cond).getExpr1(), negate(((ImpliesCondition) cond).getExpr2()));
        } else if (cond instanceof LaxCondition){
            return negate((LaxCondition) cond);
        } else {
            assert false; // shouldn't happen
            return null;
        }
    }

    private LaxCondition negate(LaxCondition laxCon) {
        //TODO: implement
        return laxCon;
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
        // TODO fix this (USE case study inv 3) shows that this is possible
        String curType = graphBuilder.getVariableType(split.get(0));
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
        for (String path: split) {
            // follow the edges to the final type node
            List<TypeEdge> typeEdges = getTypeNodeOfAttribute(type, path);

            if (typeEdges.isEmpty()){
                // the type does not have the path edge, does one of its super types contain the path edge?
                for (TypeNode superType: type.getSupertypes()) {
                    typeEdges = getTypeNodeOfAttribute(superType, path);
                    if (!typeEdges.isEmpty()){
                        break;
                    }
                }
                // if the edge does not exist in the type graph and neither in the supertypes, the given OCL expression is not correct
                if (typeEdges.isEmpty()){
                    LOGGER.severe(String.format("The outgoing edge %s does not exist for class %s", path, type));
                    throw new InvalidOCLException();
                }
            }
            type = typeEdges
                    .get(0)
                    .target();
        }

        return type;
    }

    /**
     * Given a node return the edges in which the label equals attribute
     * according to the TypeGraph
     */
    private List<TypeEdge> getTypeNodeOfAttribute (TypeNode type, String attribute) {
        return typeGraph.outEdgeSet(type).stream()
                .filter(e -> e.text().equals(attribute))
                .collect(Collectors.toList());
    }

    /**
     * The tr_N translation rules
     */
    private LaxCondition tr_NS(String expr, PlainGraph graph) {
        LaxCondition con;
        if (expr.contains(".")) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split("\\.")));

            String role = split.remove(split.size() - 1);
            TypeNode roleType = determineType(expr);

            expr = StringUtils.join(split, ".");
            TypeNode exprType = determineType(expr);

            if (roleType.getSupertypes().contains(exprType)) {
                // if exprType is an element in the clan of the roletype (clan is a type with its supertypes)
                // then it is rule42
                con = null;
            } else {
                // rule41
                PlainGraph varPrime = graphBuilder.createGraph();
                String vp = graphBuilder.addNode(varPrime, exprType.text());
                LaxCondition trn = tr_NS(expr, varPrime);

                graphBuilder.addNode(graph, vp, exprType.text());
                graphBuilder.addEdge(graph, vp, role, graphBuilder.getVarNameOfNoden0(graph));

                con = new LaxCondition(Quantifier.EXISTS, graph, trn);
            }
        } else {
            // rule40
            String vp = graphBuilder.getVarNameOfNoden0(graph);
            graphBuilder.addNode(graph, expr, graphBuilder.getVariableType(vp));
            graphBuilder.addEdge(graph, vp, EQ, expr);

            con = new LaxCondition(Quantifier.EXISTS, graph);
        }
        return con;
    }

    public Map<LaxCondition, GraphBuilder> getResults() {
        return results;
    }
}
