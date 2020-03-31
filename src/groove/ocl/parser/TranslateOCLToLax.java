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

import static groove.ocl.Groove.*;

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
    public void outALogicalExpression(ALogicalExpression node) {
        if (!node.getImplication().isEmpty()) {
            // rule8
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
                } else if (op instanceof AXorBooleanOperator) {
                    // rule7 (a xor b = (a v b) ^ -(a ^ b)
                    con1 = new AndCondition(new OrCondition(con1, con2), negate(new AndCondition(con1, con2)));
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
    public void outARelationalExpression(ARelationalExpression node) {
        // operators are {=, <>}
        if (node.getEquation() != null) {
            String expr1 = getOut(node.getCompareableExpression()).toString();
            Object expr2 = getOut(node.getEquation());
            Operator op = (Operator) getOut(((AEquation) node.getEquation()).getEquationOperator());

            if (expr2 instanceof Constant){
                // rule 15
                resetOut(node, applyRule15(node, expr1, op, (Constant) expr2));
            } else if (OCL.NULL.equals(expr2)) {
                if (op.equals(Operator.EQ)){
                    // rule13 (= null is equal to isEmpty)
                    resetOut(node, applyIsEmpty(node, expr1));
                } else if (op.equals(Operator.NEQ)){
                    // rule14 (<> null is equal to notEmpty)
                    resetOut(node, applyNotEmpty(node, expr1));
                } else {
                    assert false; // shouldn't happen
                }
            } else if (OCL.PRIMARY_OPERATIONS.contains(determineType(node, expr1).text())) {
                // if the type is primary then it's rule16
                resetOut(node, applyRule16(node, expr1, op, expr2.toString()));
            } else {
                // rule10, rule11 or rule12
                Condition condition = applyEquals(node, expr1, expr2.toString(), op);
                resetOut(node, condition);
            }
        } else {
            defaultOut(node);
        }
    }

    private Condition applyEquals(Node node, String expr1, String expr2, Operator op) {
        String t1 = determineType(node, expr1).text();
        String t2 = determineType(node, expr2).text();
        if (t1.equals(t2)) {
            PlainGraph var = graphBuilder.createGraph();
            String vn = graphBuilder.addNode(var, t1);

            PlainGraph varp = graphBuilder.createGraph();
            String vpn = graphBuilder.addNode(varp, t2);

            LaxCondition trn1 = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
            LaxCondition trn2 = tr_NS(node, expr2, graphBuilder.cloneGraph(varp));
            AndCondition andCon = new AndCondition(trn1, trn2);

            var = graphBuilder.mergeGraphs(var, varp);

            if (op.equals(Operator.NEQ)) {
                // rule12
                graphBuilder.addEdge(var, vn, String.format("%s:%s", NOT, EQ), vpn);
            } else {
                // rule10
                graphBuilder.addEdge(var, vn, EQ, vpn);
            }
            return new LaxCondition(Quantifier.EXISTS, var, andCon);
            // TODO: implement rule 11 T is Set(T)
        } else {
            assert false; //shouln't happen
            return null;
        }
    }

    @Override
    public void outACompareableExpression(ACompareableExpression node) {
        // operators are {<, <=, =>, >}
        if (node.getComparison() != null){
            String expr1 = getOut(node.getAdditiveExpression()).toString();
            Object expr2 = getOut(((AComparison) node.getComparison()).getAdditiveExpression());
            Operator op = (Operator) getOut(((AComparison) node.getComparison()).getCompareOperator());

            if (expr2 instanceof Constant) {
                // so its rule15
                resetOut(node, applyRule15(node, expr1, op, (Constant) expr2));
            } else {
                // its rule16
               resetOut(node, applyRule16(node, expr1, op, expr2.toString()));
            }
        } else {
            defaultOut(node);
        }
    }

    /**
     * Given the expr1, operator and constant
     * Apply transformation rule15
     */
    private LaxCondition applyRule15(Node node, String expr1, Operator op, Constant expr2) {
        Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType = determineTypeAndAttribute(node, expr1);
        expr1 = expr1AttrType.getFirst().getFirst();

        PlainGraph attrGraph = graphBuilder.createGraph();
        String varName = graphBuilder.addNode(attrGraph, expr1AttrType.getFirst().getSecond().text());
        graphBuilder.addAttributedGraph(attrGraph, varName, expr1AttrType.getSecond(), op, expr2);

        LaxCondition trn = tr_NS(node, expr1, graphBuilder.cloneGraph(attrGraph));

        // given the values create the right LaxCondition
        return new LaxCondition(Quantifier.EXISTS, attrGraph, trn);
    }

    /**
     * Given the expr1, operator and expr2
     * Apply transformation rule16
     */
    private Condition applyRule16(Node node, String expr1, Operator op, String expr2) {
        Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType = determineTypeAndAttribute(node, expr1);
        expr1 = expr1AttrType.getFirst().getFirst();
        Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr2AttrType = determineTypeAndAttribute(node, expr2);
        expr2 = expr2AttrType.getFirst().getFirst();

        PlainGraph var = graphBuilder.createGraph();
        String attr1Name = graphBuilder.addNodeWithAttribute(var, expr1AttrType);

        PlainGraph attrGraph1 = graphBuilder.cloneGraph(var);

        PlainGraph varp = graphBuilder.createGraph();
        String attr2Name = graphBuilder.addNodeWithAttribute(varp, expr2AttrType);

        // instead of using a variable x, just connect with attr1Name
        graphBuilder.addNode(varp, attr1Name, expr1AttrType.getSecond().getSecond().text());
        graphBuilder.addProductionRule(varp, attr1Name, expr2AttrType.getSecond().getSecond().text(), op, attr2Name);

        PlainGraph attrGraph2 = graphBuilder.cloneGraph(varp);

        LaxCondition trn1 = tr_NS(node, expr1, attrGraph1);
        LaxCondition trn2 = tr_NS(node, expr2, attrGraph2);
        AndCondition andCon = new AndCondition(trn1, trn2);

        return new LaxCondition(Quantifier.EXISTS, graphBuilder.mergeGraphs(var, varp), andCon);
    }

    @Override
    public void outAPrefixedUnaryExpression(APrefixedUnaryExpression node) {
        if (node.getUnaryOperator() != null) {
            // rule4
            resetOut(node, negate((Condition) getOut(node)));
        } else {
            defaultOut(node);
        }
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
            if (propertyCall.getPropertyCallParameters() == null) {
                // check if the propertycall is one of operations we have to transform
                resetOut(node);
                return;
            }

            String operation = (String) getOut(propertyCall.getPathName());
            String expr1 = (String) getOut(node.getPrimaryExpression());

            @SuppressWarnings("unchecked") // it's a clone so this can't go wrong
            LinkedList<PPropertyInvocation> clone = (LinkedList<PPropertyInvocation>) node.getPropertyInvocation().clone();
            clone.removeLast(); // we want to remove the last therefore we needed the clone
            for (PPropertyInvocation p : clone) {
                expr1 = expr1.concat(String.format(".%s", getOut(p)));
            }

            if (OCL.INCLUDES_ALL.equals(operation) || OCL.INCLUDES.equals(operation)){
                // rule19 || rule21
                String expr2 = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());

                PlainGraph var = graphBuilder.createGraph();
                graphBuilder.addNode(var, determineType(node, expr1).text());

                LaxCondition trn1 = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
                LaxCondition trn2 = tr_NS(node, expr2, graphBuilder.cloneGraph(var));

                Condition condition = new ImpliesCondition(trn2, trn1);
                resetOut(node, new LaxCondition(Quantifier.FORALL, var, condition));
            } else if (OCL.EXCLUDES_ALL.equals(operation) || OCL.EXCLUDES.equals(operation)) {
                //rule20 || rule22
                String expr2 = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());

                PlainGraph var = graphBuilder.createGraph();
                graphBuilder.addNode(var, determineType(node, expr1).text());

                LaxCondition trn1 = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
                LaxCondition trn2 = tr_NS(node, expr2, graphBuilder.cloneGraph(var));

                Condition condition = new ImpliesCondition(trn2, negate(trn1, false, true));
                resetOut(node, new LaxCondition(Quantifier.FORALL, var, condition));
            } else if (OCL.NOT_EMPTY.equals(operation)) {
                // rule23
                resetOut(node, applyNotEmpty(node, expr1));
            } else if (OCL.IS_EMPTY.equals(operation)) {
                // rule24
                resetOut(node, applyIsEmpty(node, expr1));
            } else if (OCL.OCL_IS_KIND_OF.equals(operation)) {
                //rule35
                String T = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());

                PlainGraph var = graphBuilder.createGraph();
                graphBuilder.addNode(var, T);

                LaxCondition trn = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, trn));
            } else if (OCL.OCL_IS_TYPE_OF.equals(operation)) {
                // rule36
                String T = (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());

                PlainGraph var = graphBuilder.createGraph();
                graphBuilder.addNode(var, String.format("%s%s", SHARP_TYPE, T));

                LaxCondition trn = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, trn));
            } else if (OCL.EXISTS.equals(operation)) {
                // rule17
                PlainGraph var = createVariableFromDeclarator(propertyCall);

                LaxCondition trn = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
                Condition tre = (Condition) getOut(node.getPropertyInvocation().get(1));

                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, new AndCondition(trn, tre)));
            } else if (OCL.FORALL.equals(operation)) {
                // rule18
                PlainGraph var = createVariableFromDeclarator(propertyCall);

                LaxCondition trn = tr_NS(node, expr1, graphBuilder.cloneGraph(var));
                Condition tre = (Condition) getOut(node.getPropertyInvocation().get(1));

                resetOut(node, new LaxCondition(Quantifier.FORALL, var, new ImpliesCondition(trn, tre)));
            } else {
                assert false; //This operation is not implemented
            }
        } else if (getOut(node) instanceof Condition) {
            defaultOut(node);
        } else {
            resetOut(node);
        }
    }

    /**
     * Applies rule23
     */
    private LaxCondition applyNotEmpty(Node node, String expr) {
        PlainGraph var = graphBuilder.createGraph();
        graphBuilder.addNode(var, determineType(node, expr).text());

        LaxCondition trn = tr_NS(node, expr, graphBuilder.cloneGraph(var));
        return new LaxCondition(Quantifier.EXISTS, var, trn);
    }

    /**
     * Applies rule24
     */
    private LaxCondition applyIsEmpty(Node node, String expr) {
        PlainGraph var = graphBuilder.createGraph();
        String varn = graphBuilder.addNode(var, determineType(node, expr).text());

        LaxCondition trn = tr_NS(node, expr, graphBuilder.cloneGraph(var));
        graphBuilder.addEdge(var, varn, String.format("%s:", NOT), varn);
        return new LaxCondition(Quantifier.EXISTS, var, trn);
    }

    /**
     * Create a variable graph from the declarator
     */
    private PlainGraph createVariableFromDeclarator(APropertyCall propertyCall) {
        AConcreteDeclarator declarator = (AConcreteDeclarator) ((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getDeclarator();
        String varn = declarator.getActualParameterList().toString().trim();
        String T = ((ASimpleTypePostfix) declarator.getSimpleTypePostfix()).getSimpleTypeSpecifier().toString().trim();

        PlainGraph var = graphBuilder.createGraph();
        graphBuilder.addNode(var, varn, T);
        return var;
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
        Condition ifCond = (Condition) getOut(node.getCondition());
        Condition elseCond = negate(graphBuilder.cloneCondition(ifCond));
        Condition thenBranch = (Condition) getOut(node.getThenBranch());

        AndCondition ifThen = new AndCondition(ifCond, thenBranch);

        Object elseBranch = getOut(node.getElseBranch());
        if (elseBranch instanceof Condition) {
            AndCondition elseThen = new AndCondition(elseCond, (Condition) elseBranch);
            resetOut(node, new OrCondition(ifThen, elseThen));
        } else if (elseBranch instanceof BooleanConstant) {
            if (elseBranch.equals(BooleanConstant.TRUE)) {
                // else is true, then the elsecond should hold
                resetOut(node, new OrCondition(ifThen, elseCond));
            } else {
                // else is false, so the rule should only contain the ifThen else the rule shouldn't match
                resetOut(node, ifThen);
            }
        } else {
            assert false; //shouldn't happen
        }
    }

    /**
     * Create the negation of an Condition
     * @param cond  The condition that gets negated
     * @return      The negated condition
     */
    private Condition negate(Condition cond) {
        if (cond instanceof AndCondition) {
            // -(a ∧ b) = -a v -b
            OrCondition or = new OrCondition(negate(((AndCondition) cond).getExpr1()), negate(((AndCondition) cond).getExpr2()));
            return new LaxCondition(Quantifier.FORALL, graphBuilder.createGraph(), or);
        } else if (cond instanceof OrCondition){
            // -(a v b) = -a ∧ -b
            return new AndCondition(negate(((OrCondition) cond).getExpr1()), negate(((OrCondition) cond).getExpr2()));
        } else if (cond instanceof ImpliesCondition) {
            // -(a -> b) = a ∧ -b
            return new AndCondition(((ImpliesCondition) cond).getExpr1(), negate(((ImpliesCondition) cond).getExpr2()));
        } else if (cond instanceof LaxCondition){
            return negate((LaxCondition) cond);
        } else {
            assert false; // shouldn't happen
            return null;
        }
    }

    private LaxCondition negate(LaxCondition laxCon) {
        return negate(laxCon, true, false);
    }

    private LaxCondition negate(LaxCondition laxCon, boolean negateNodes, boolean negateEdges) {
        graphBuilder.negate(laxCon.getGraph(), negateNodes, negateEdges);
        return laxCon;
    }

    @Override
    public void outAEquationOperator(AEquationOperator node) {
        resetOut(node, Operator.EQ);
    }

    @Override
    public void outAInEquationOperator(AInEquationOperator node) {
        resetOut(node, Operator.NEQ);
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
    private Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> determineTypeAndAttribute(Node node, String expr) {
        String[] split = expr.split("\\.");

        String attr = split[split.length-1];
        TypeNode typeAttr = determineType(node, expr);
        Tuple2<String, TypeNode> attrType = new Tuple2<>(attr, typeAttr);

        expr = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));
        TypeNode type = determineType(node, expr);
        Tuple2<String, TypeNode> exprType = new Tuple2<>(expr, type);

        return new Tuple2<>(exprType, attrType);
    }

    /**
     * Given a navigation expression (e.g. self.age) get the corresponding TypeNode
     * @param expr      A navigation expression String, separated by dots
     * @return          The corresponding TypeNode from the TypeGraph
     */
    private TypeNode determineType(Node node, String expr) {
        List<String> split = new ArrayList<>();
        Collections.addAll(split, expr.split("\\."));

        // TODO check if an expression could start without a custom variable (e.g. self), if so a nullpointer exists here
        // TODO fix this (USE case study inv 3) shows that this is possible
        String curType = graphBuilder.getVariableType(split.get(0));
        if (curType == null){
            // if curType is null; it may happen that the variable (split.get(0)) is defined in a declarator
            // which is the case when we are inside an exists or forall
            curType = getDeclarator(node);
        }
        split.remove(split.get(0));

        // find the first node
        final String finalCurType = curType;
        List<TypeNode> types = typeGraph.nodeSet().stream()
                .filter(n -> n.text().equals(finalCurType))
                .collect(Collectors.toList());

        if (types.isEmpty()) {
            LOGGER.severe(String.format("In the type graph is no %s-type defined", curType));
            throw new InvalidOCLException();
        }

        TypeNode type = types.get(0);
        for (String path: split) {
            // check if the path is an OCL cast, because that will change the way we have to follow the edges
            if (path.contains(OCL.OCL_AS_TYPE)){
                String t = path.substring(path.indexOf("(")+1, path.indexOf(")"));
                type = typeGraph.getNode(String.format("%s:%s", TYPE, t));
            } else {
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
        }

        return type;
    }

    private String getDeclarator(Node node) {
        // it seems that the variable doesn't exist yet, probably we are inside an exists or forall;
        // find the declarator to determine the type
        if (node instanceof APropertyCallParameters) {
            return ((ASimpleTypeSpecifier) ((ASimpleTypePostfix) ((AConcreteDeclarator) ((APropertyCallParameters) node).getDeclarator()).getSimpleTypePostfix()).getSimpleTypeSpecifier()).toString().trim();
        } else {
            return getDeclarator(node.parent());
        }
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
    private LaxCondition tr_NS(Node node, String expr, PlainGraph graph) {
        if (expr.contains(".")) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split("\\.")));
            String role = split.remove(split.size() - 1);
            expr = StringUtils.join(split, ".");

            if (role.contains(OCL.OCL_AS_TYPE)) {
                // rule40
                return tr_NS(node, expr, graph);
            } else {
                //rule41
                TypeNode exprType = determineType(node, expr);

                PlainGraph varPrime = graphBuilder.createGraph();
                String vp = graphBuilder.addNode(varPrime, exprType.text());
                LaxCondition trn = tr_NS(node, expr, varPrime);

                graphBuilder.addNode(graph, vp, exprType.text());
                graphBuilder.addEdge(graph, vp, role, graphBuilder.getVarNameOfNoden0(graph));

                return new LaxCondition(Quantifier.EXISTS, graph, trn);
            }
        } else {
            // rule39
            String vp = graphBuilder.getVarNameOfNoden0(graph);
            graphBuilder.addNode(graph, expr, graphBuilder.getVariableType(vp));
            graphBuilder.addEdge(graph, vp, EQUIV, expr);

            return new LaxCondition(Quantifier.EXISTS, graph);
        }
    }

    public Map<LaxCondition, GraphBuilder> getResults() {
        return results;
    }
}
