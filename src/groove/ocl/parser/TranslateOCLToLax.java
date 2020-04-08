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

            resetOut(node, applyComparison(node, expr1, op, expr2));
        } else {
            defaultOut(node);
        }
    }

    @Override
    public void outACompareableExpression(ACompareableExpression node) {
        // operators are {<, <=, =>, >}
        if (node.getComparison() != null){
            String expr1 = getOut(node.getAdditiveExpression()).toString();
            Object expr2 = getOut(((AComparison) node.getComparison()).getAdditiveExpression());
            Operator op = (Operator) getOut(((AComparison) node.getComparison()).getCompareOperator());

            resetOut(node, applyComparison(node, expr1, op, expr2));
        } else {
            defaultOut(node);
        }
    }

    private Condition applyComparison(Node node, String expr1, Operator op, Object expr2) {
        if (expr1.contains(OCL.SIZE)){
            // it's one of the size rules
            return applySize(node, expr1, (IntConstant) expr2, op);
        } else if (expr2 instanceof Constant) {
            // so its rule15
            return applyRule15(node, expr1, op, (Constant) expr2);
        } else if (OCL.NULL.equals(expr2)) {
            if (op.equals(Operator.EQ)){
                // rule13 (= null is equal to isEmpty)
                return applyIsEmpty(node, expr1);
            } else if (op.equals(Operator.NEQ)){
                // rule14 (<> null is equal to notEmpty)
                return applyNotEmpty(node, expr1);
            } else {
                assert false; //shouldn't happen
                return null;
            }
        } else if (OCL.PRIMARY_OPERATIONS.contains(determineType(node, expr1).text())) {
            // its rule16
            return applyRule16(node, expr1, expr2.toString(), op);
        } else {
            // rule10, rule11 or rule12
            return applyEquals(node, expr1, expr2.toString(), op);
        }
    }

    private Condition applySize(Node node, String expr1, IntConstant expr2, Operator op) {
        int n = expr2.getConstant();
        if (op.equals(Operator.GTEQ)){
            // rule26
            return applySize(node, expr1, n);
        } else if (op.equals(Operator.GT)){
            // rule27
            return applySize(node, expr1, n+1);
        } else if (op.equals(Operator.EQ)){
            // rule28
            return new AndCondition(applySize(node, expr1, n), negate(applySize(node, expr1, n+1)));
        } else if (op.equals(Operator.LTEQ)){
            // rule29
            return negate(applySize(node, expr1, n+1));
        } else if (op.equals(Operator.LT)){
            // rule30
            return negate(applySize(node, expr1, n));
        } else if (op.equals(Operator.NEQ)){
            // rule31
            return negate(new AndCondition(applySize(node, expr1, n), negate(applySize(node, expr1, n+1))));
        } else {
            assert false; //shouldn't happen
            return null;
        }
    }

    /**
     * Given the expr1, and constant n
     * Apply transformation rule26
     */
    private LaxCondition applySize(Node node, String expr1, int n) {
        expr1 = expr1.split(OCL.ARROW)[0];
        TypeNode t = determineType(node, expr1);

        // Create all nodes in vars
        PlainGraph vars = graphBuilder.createGraph();
        List<String> varNames = new ArrayList<>();

        // start with an empty LaxCondition, with simplification this one will disappear
        Condition con = new LaxCondition(Quantifier.EXISTS, graphBuilder.createGraph());
        for (int i = 0; i < n; i++) {
            // create var
            PlainGraph var = graphBuilder.createGraph();
            String varName = graphBuilder.addNode(var, t.text());
            varNames.add(varName);

            // connect it
            Condition trs = tr_S(node, expr1, graphBuilder.cloneGraph(var));
            con = new AndCondition(con, trs);

            // merge the vars
            vars = graphBuilder.mergeGraphs(vars, var);
        }

        // create the != edges
        PlainGraph neq = graphBuilder.cloneGraph(vars);
        for (int i = 0; i < n-1; i++) {
            for (int j = i + 1; j < n; j++) {
                graphBuilder.addEdge(neq, varNames.get(i), String.format("%s:%s", NOT, EQ) ,varNames.get(j));
            }
        }
        LaxCondition neqCon = new LaxCondition(Quantifier.EXISTS, neq);
        return new LaxCondition(Quantifier.EXISTS, vars, new AndCondition(neqCon, con));
    }

    /**
     * Given the expr1, operator and constant
     * Apply transformation rule15
     */
    private LaxCondition applyRule15(Node node, String expr1, Operator op, Constant expr2) {
        Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> expr1AttrType = determineTypeAndAttribute(node, expr1);
        if (!expr1.contains(OCL.MIN)) {
            expr1 = expr1AttrType.getFirst().getFirst();
        }

        PlainGraph attrGraph = graphBuilder.createGraph();
        String varName = graphBuilder.addNode(attrGraph, expr1AttrType.getFirst().getSecond().text());
        graphBuilder.addAttributedGraph(attrGraph, varName, expr1AttrType.getSecond(), op, expr2);

        Condition trn = tr_N(node, expr1, graphBuilder.cloneGraph(attrGraph));

        // given the values create the right LaxCondition
        return new LaxCondition(Quantifier.EXISTS, attrGraph, trn);
    }

    /**
     * Given the expr1, operator and expr2
     * Apply transformation rule16
     */
    private Condition applyRule16(Node node, String expr1, String expr2, Operator op) {
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

        Condition trn1 = tr_N(node, expr1, attrGraph1);
        Condition trn2 = tr_N(node, expr2, attrGraph2);
        AndCondition andCon = new AndCondition(trn1, trn2);

        return new LaxCondition(Quantifier.EXISTS, graphBuilder.mergeGraphs(var, varp), andCon);
    }

    private Condition applyEquals(Node node, String expr1, String expr2, Operator op) {
        String t1 = determineType(node, expr1).text();
        String t2 = determineType(node, expr2).text();
        if (t1.equals(t2)) {
            PlainGraph var = graphBuilder.createGraph();
            String vn = graphBuilder.addNode(var, t1);

            PlainGraph varp = graphBuilder.createGraph();
            String vpn = graphBuilder.addNode(varp, t2);

            Condition trn1 = tr_N(node, expr1, graphBuilder.cloneGraph(var));
            Condition trn2 = tr_N(node, expr2, graphBuilder.cloneGraph(varp));
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
            APropertyCall propertyCall = getAPropertyCall(node.getPropertyInvocation().getLast());
            assert propertyCall != null;
            if (propertyCall.getPropertyCallParameters() == null) {
                // check if the propertycall is one of operations we have to transform
                // self.editedBook, the editedBook is also an propertyInvocation, one we want to skip
                resetOut(node);
                return;
            }

            // get the name of the operation and determine which rule to apply
            String operation = (String) getOut(propertyCall.getPathName());
            String expr1 = (String) getOut(node.getPrimaryExpression());

            // create the complete expr1 value
            for (Iterator<PPropertyInvocation> i = node.getPropertyInvocation().iterator(); i.hasNext();) {
                PPropertyInvocation n = i.next();
                if (i.hasNext()){
                    expr1 = expr1.concat( n.toString().replaceAll(" ", ""));
                }
            }

            // apply the correct translation rule
            if (OCL.INCLUDES_ALL.equals(operation) || OCL.INCLUDES.equals(operation)) {
                // rule20 || rule22
                resetOut(node, applyIncludes(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.EXCLUDES_ALL.equals(operation) || OCL.EXCLUDES.equals(operation)) {
                //rule21 || rule23
                resetOut(node, applyExcludes(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.NOT_EMPTY.equals(operation)) {
                // rule24
                resetOut(node, applyNotEmpty(node, expr1));
            } else if (OCL.IS_EMPTY.equals(operation)) {
                // rule25
                resetOut(node, applyIsEmpty(node, expr1));
            } else if (OCL.OCL_IS_KIND_OF.equals(operation)) {
                //rule36
                resetOut(node, applyOclIsKindOf(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.OCL_IS_TYPE_OF.equals(operation)) {
                // rule37
                resetOut(node, applyOclIsTypeOf(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.EXISTS.equals(operation)) {
                // rule18
                resetOut(node, applyExists(node, expr1, propertyCall));
            } else if (OCL.FORALL.equals(operation)) {
                // rule19 || rule20
                resetOut(node, applyForall(node, expr1, propertyCall));
            } else if (OCL.IS_UNIQUE.equals(operation)) {
                // rule35
                resetOut(node, applyIsUnique(node, expr1, (String) getOut(node)));
            } else if (OCL.SIZE.equals(operation) || OCL.MIN.equals(operation)) {
                // operation size has to be compared with a constant, the constant is not available at this point in the tree
                resetOut(node);
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
     * Given the propertycall return the expr2 parameter
     */
    private String getExpr2FromPropertyCall(APropertyCall propertyCall) {
        return (String) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());
    }

    private LaxCondition applyIncludes(Node node, String expr1, String expr2) {
        PlainGraph var = graphBuilder.createVar(determineType(node, expr1).text());

        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(var));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(var));

        Condition condition = new ImpliesCondition(trs2, trs1);
        return new LaxCondition(Quantifier.FORALL, var, condition);
    }

    private LaxCondition applyExcludes(Node node, String expr1, String expr2) {
        PlainGraph var = graphBuilder.createVar(determineType(node, expr1).text());

        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(var));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(var));

        Condition condition = new ImpliesCondition(trs2, negate(trs1, false, true));
        return new LaxCondition(Quantifier.FORALL, var, condition);
    }

    private LaxCondition applyNotEmpty(Node node, String expr) {
        PlainGraph var = graphBuilder.createVar(determineType(node, expr).text());

        Condition trs = tr_S(node, expr, graphBuilder.cloneGraph(var));
        return new LaxCondition(Quantifier.EXISTS, var, trs);
    }

    private LaxCondition applyIsEmpty(Node node, String expr) {
        PlainGraph var = graphBuilder.createGraph();
        String varn = graphBuilder.addNode(var, determineType(node, expr).text());

        Condition trs = tr_S(node, expr, graphBuilder.cloneGraph(var));
        graphBuilder.addEdge(var, varn, String.format("%s:", NOT), varn);
        return new LaxCondition(Quantifier.EXISTS, var, trs);
    }

    private LaxCondition applyOclIsKindOf(Node node, String expr1, String T) {
        PlainGraph var = graphBuilder.createVar(T);

        Condition trn = tr_N(node, expr1, graphBuilder.cloneGraph(var));
        return new LaxCondition(Quantifier.EXISTS, var, trn);
    }

    private LaxCondition applyOclIsTypeOf(Node node, String expr1, String T) {
        PlainGraph var = graphBuilder.createVar(String.format("%s%s", SHARP_TYPE, T));

        Condition trn = tr_N(node, expr1, graphBuilder.cloneGraph(var));
        return new LaxCondition(Quantifier.EXISTS, var, trn);
    }

    private LaxCondition applyExists(APostfixExpression node, String expr1, APropertyCall propertyCall) {
        Tuple2<PlainGraph, Condition> variableFromDeclarator = createVariableFromDeclarator(node, expr1, propertyCall);
        Condition tre = (Condition) getOut(node.getPropertyInvocation().get(1));
        return new LaxCondition(Quantifier.EXISTS, variableFromDeclarator.getFirst(), new AndCondition(variableFromDeclarator.getSecond(), tre));
    }

    private LaxCondition applyForall(APostfixExpression node, String expr1, APropertyCall propertyCall) {
        Tuple2<PlainGraph, Condition> variableFromDeclarator = createVariableFromDeclarator(node, expr1, propertyCall);
        Condition tre = (Condition) getOut(node.getPropertyInvocation().get(1));
        return new LaxCondition(Quantifier.FORALL, variableFromDeclarator.getFirst(), new ImpliesCondition(variableFromDeclarator.getSecond(), tre));
    }

    private LaxCondition applyIsUnique(APostfixExpression node, String expr, String attr) {
        // determine type and name of first variable
        String T = determineType(node, expr).toString();

        // create var1
        PlainGraph var1 = graphBuilder.createGraph();
        String var1Name = graphBuilder.addNode(var1, T);
        Condition trs1 = tr_S(node, expr, graphBuilder.cloneGraph(var1));

        //create var2
        PlainGraph var2 = graphBuilder.createGraph();
        String var2Name = graphBuilder.addNode(var2, T);
        Condition trs2 = tr_S(node, expr, graphBuilder.cloneGraph(var2));

        // merge the vars
        PlainGraph vars = graphBuilder.mergeGraphs(var1, var2);

        // create the expr1 <> expr2 -> expr1.attr <> expr2.attr
        Condition neq1 = applyEquals(node, var1Name, var2Name, Operator.NEQ);
        Condition neq2 = applyRule16(node, String.format("%s.%s", var1Name, attr), String.format("%s.%s", var2Name, attr), Operator.NEQ);
        Condition tre = new ImpliesCondition(neq1, neq2);

        return new LaxCondition(Quantifier.FORALL, vars, new ImpliesCondition(new AndCondition(trs1, trs2), tre));
    }

    /**
     * Create the graph with all the variables
     * and the tr_S condition for every variable such that the variables are connected
     * @return A Tuple2 with in the first the vars PlainGraph and in the second the tr_S condition
     */
    private Tuple2<PlainGraph, Condition> createVariableFromDeclarator(APostfixExpression node, String expr1, APropertyCall propertyCall) {
        AConcreteDeclarator declarator = (AConcreteDeclarator) ((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getDeclarator();
        AActualParameterList actualParameterList = (AActualParameterList) declarator.getActualParameterList();

        // determine type and name of first variable
        String T = ((ASimpleTypePostfix) declarator.getSimpleTypePostfix()).getSimpleTypeSpecifier().toString().trim();
        String varn = actualParameterList.getExpression().toString().trim();

        // create a graph with first variable
        PlainGraph vars = graphBuilder.createVar(varn, T);
        Condition trs = tr_S(node, expr1, graphBuilder.cloneGraph(vars));
        for (PNextExpr expr : actualParameterList.getNextExpr()) {
            // if there are more declared variables create the variable graph and create the path according expr1
            // apply rule20
            PlainGraph var2 = graphBuilder.createVar(((ANextExpr) expr).getExpression().toString().trim(), T);
            Condition trs2 = tr_S(node, expr1, graphBuilder.cloneGraph(var2));

            // create the new trs condition and merge the variable to the complete graph of variables
            trs = new AndCondition(trs, trs2);
            vars = graphBuilder.mergeGraphs(vars, var2);
        }
        return new Tuple2<>(vars, trs);
    }

    /**
     * returns the _propertyCall_ of PPropertyInvocation because the generated code does not have that getter in the right abstract class
     */
    private APropertyCall getAPropertyCall(PPropertyInvocation node) {
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
        return negate(cond, true, false);
    }

    private Condition negate(Condition cond, boolean negateNodes, boolean negateEdges) {
        if (cond instanceof AndCondition) {
            // -(a ∧ b) = -a v -b
            OrCondition or = new OrCondition(
                    negate(((AndCondition) cond).getExpr1(), negateNodes, negateEdges),
                    negate(((AndCondition) cond).getExpr2(), negateNodes, negateEdges));
            return new LaxCondition(Quantifier.FORALL, graphBuilder.createGraph(), or);
        } else if (cond instanceof OrCondition){
            // -(a v b) = -a ∧ -b
            return new AndCondition(
                    negate(((OrCondition) cond).getExpr1(), negateNodes, negateEdges),
                    negate(((OrCondition) cond).getExpr2(), negateNodes, negateEdges));
        } else if (cond instanceof ImpliesCondition) {
            // -(a -> b) = a ∧ -b
            return new AndCondition(
                    ((ImpliesCondition) cond).getExpr1(),
                    negate(((ImpliesCondition) cond).getExpr2(), negateNodes, negateEdges));
        } else if (cond instanceof LaxCondition){
            return negate((LaxCondition) cond, negateNodes, negateEdges);
        } else {
            assert false; // shouldn't happen
            return null;
        }
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

        // in case the attr contains ->min() or ->max()
        String attr = split[split.length-1].split("->")[0];
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
        Collections.addAll(split, expr.split("(\\.|->|\\(|\\))+"));

        String curType = graphBuilder.getVariableType(split.get(0));
        if (curType == null){
            if (split.size() > 1 && OCL.ALL_INSTANCES.equals(split.get(1))) {
                // if the expression does not start with an custom variable, check if it starts with allInstances
                curType = split.get(0);
                split.remove(split.get(1));
            } else {
                // if curType = null and it is not an allInstances; it may happen that the variable (split.get(0)) is defined in a declarator
                // which is the case when we are inside an exists or forall
                curType = getTypeOfDeclarator(node);
            }

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
        for (Iterator<String> i = split.iterator(); i.hasNext();) {
            String path = i.next();
            // check if the path is an OCL cast, because that will change the way we have to follow the edges
            if (path.equals(OCL.OCL_AS_TYPE)){
                String t = split.get(split.indexOf(path) + 1);
                type = typeGraph.getNode(String.format("%s:%s", TYPE, t));

                // the next is handled already, skip it
                i.next();
            } else if(OCL.SET_OPERATIONS.contains(path)) {
                return type;
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

    private String getTypeOfDeclarator(Node node) {
        // it seems that the variable doesn't exist yet, probably we are inside an exists or forall;
        // find the declarator to determine the type
        if (node instanceof APropertyCallParameters) {
            return ((ASimpleTypeSpecifier) ((ASimpleTypePostfix) ((AConcreteDeclarator) ((APropertyCallParameters) node).getDeclarator()).getSimpleTypePostfix()).getSimpleTypeSpecifier()).toString().trim();
        } else {
            return getTypeOfDeclarator(node.parent());
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
    private Condition tr_N(Node node, String expr, PlainGraph graph) {
        if (expr.contains(OCL.MIN)) {
            LaxCondition min = applyMin(node, expr, graphBuilder.cloneGraph(graph));
            expr = determineTypeAndAttribute(node, expr).getFirst().getFirst();
            Condition trn = tr_N(node, expr, graph);
            return new AndCondition(trn, min);
        } else if (expr.contains(".")) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split("\\.")));
            String role = split.remove(split.size() - 1);
            expr = StringUtils.join(split, ".");

            if (role.contains(OCL.OCL_AS_TYPE)) {
                // rule40
                return tr_N(node, expr, graph);
            } else {
                //rule41
                return applyNavigationRole(node, expr, role, graph);
            }
        } else {
            // rule39
            String vp = graphBuilder.getVarNameOfNoden0(graph);
            graphBuilder.addNode(graph, expr, graphBuilder.getVariableType(vp));
            graphBuilder.addEdge(graph, vp, EQUIV, expr);

            return new LaxCondition(Quantifier.EXISTS, graph);
        }
    }

    private LaxCondition applyMin(Node node, String expr, PlainGraph graph) {
        // determine type and name of first variable
        Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> exprAttrType = determineTypeAndAttribute(node, expr);

        // split of attr
        String[] split = expr.split("\\.");
        String attr = split[split.length-1];
        expr = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));

        // var1
        String attr1Name = graphBuilder.getVarNameOfNodeN(graph, 1);

        //create var2
        PlainGraph var2 = graphBuilder.createGraph();
        String var2Name = graphBuilder.addNode(var2, exprAttrType.getFirst().getSecond().text());
        String attr2Name = graphBuilder.addNode(var2, exprAttrType.getSecond().getSecond().text());
        graphBuilder.addEdge(var2, var2Name, exprAttrType.getSecond().getFirst(), attr2Name);
        Condition trs = tr_N(node, expr, graphBuilder.cloneGraph(var2));

        // create the var1 <> var2 -> var1.attr < var2.attr
        PlainGraph c = graphBuilder.mergeGraphs(graph, var2);
        graphBuilder.addProductionRule(c, attr1Name, exprAttrType.getSecond().getSecond().text(), Operator.LTEQ, attr2Name);
        LaxCondition tre = new LaxCondition(Quantifier.EXISTS, c);

        return new LaxCondition(Quantifier.FORALL, var2, new ImpliesCondition(trs, tre));
    }

    private LaxCondition applyNavigationRole(Node node, String expr, String role, PlainGraph graph) {
        TypeNode exprType = determineType(node, expr);

        PlainGraph varPrime = graphBuilder.createGraph();
        String vp = graphBuilder.addNode(varPrime, exprType.text());
        Condition trn = tr_N(node, expr, varPrime);

        graphBuilder.addNode(graph, vp, exprType.text());
        graphBuilder.addEdge(graph, vp, role, graphBuilder.getVarNameOfNoden0(graph));

        return new LaxCondition(Quantifier.EXISTS, graph, trn);
    }

    private Condition tr_S(Node node, String expr, PlainGraph graph) {
        if (expr.contains(OCL.ARROW)) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split(OCL.ARROW)));

            // determine the operation and expr2
            String[] operationCall = split.remove(split.size() - 1).split("([()])+");
            String operation =  operationCall[0];
            String expr2 = operationCall[1];

            // remove the selected operation from expr
            expr = StringUtils.join(split, OCL.ARROW);

            // determine which translation rule to apply
            if (OCL.UNION.equals(operation) || OCL.INCLUDING.equals(operation)) {
                // rule45 || rule49
                return applyUnion(node, expr, expr2, graph);
            } else if (OCL.INTERSECTION.equals(operation) || OCL.EXCLUDING.equals(operation)) {
                // rule46 || rule50
                return applyIntersection(node, expr, expr2, graph);
            } else if (OCL.MINUS.equals(operation)) {
                // rule47
                return applyMinus(node, expr, expr2, graph);
            } else if (OCL.SYMMETRICDIFFERENCE.equals(operation)) {
                // rule48
                Condition union = applyUnion(node, expr, expr2, graph);
                Condition intersect = applyIntersection(node, expr, expr2, graph);
                return new AndCondition(union, negate(intersect));
            }
        } else if (expr.contains(".")) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split("\\.")));
            String role = split.remove(split.size() - 1);
            expr = StringUtils.join(split, ".");

            if (role.contains(OCL.ALL_INSTANCES)){
                //rule43
                return new LaxCondition(Quantifier.EXISTS, graph);
            } else {
                //rule42
                return applyNavigationRole(node, expr, role, graph);
            }
        }
        assert false; //shouldn't happen
        return null;
    }

    private Condition applyUnion(Node node, String expr1, String expr2, PlainGraph graph) {
        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(graph));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(graph));
        return new OrCondition(trs1, trs2);
    }

    private Condition applyIntersection(Node node, String expr1, String expr2, PlainGraph graph) {
        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(graph));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(graph));
        return new AndCondition(trs1, trs2);
    }

    private Condition applyMinus(Node node, String expr1, String expr2, PlainGraph graph) {
        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(graph));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(graph));
        return new AndCondition(trs1, negate(trs2));
    }

    public Map<LaxCondition, GraphBuilder> getResults() {
        return results;
    }
}
