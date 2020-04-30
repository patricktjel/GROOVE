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
    private final TypeGraph typeGraph;
    
    private GraphBuilder graphBuilder;

    private final Map<LaxCondition, GraphBuilder> results;

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
                    Condition clCon1 = graphBuilder.cloneCondition(con1);
                    Condition clCon2 = graphBuilder.cloneCondition(con2);

                    con1 = new AndCondition(new OrCondition(con1, con2), negate(new AndCondition(clCon1, clCon2)));
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
            Object expr1 = getOut(node.getCompareableExpression());
            Object expr2 = getOut(node.getEquation());
            Operator op = (Operator) getOut(((AEquation) node.getEquation()).getEquationOperator());

            resetOut(node, applyComparison((ACompareableExpression) node.getCompareableExpression(), expr1, op, expr2));
        } else {
            defaultOut(node);
        }
    }

    @Override
    public void outACompareableExpression(ACompareableExpression node) {
        // operators are {<, <=, =>, >}
        if (node.getComparison() != null){
            Object expr1 = getOut(node.getAdditiveExpression());
            Object expr2 = getOut(((AComparison) node.getComparison()).getAdditiveExpression());
            Operator op = (Operator) getOut(((AComparison) node.getComparison()).getCompareOperator());

            resetOut(node, applyComparison(node, expr1, op, expr2));
        } else {
            defaultOut(node);
        }
    }

    private Condition applyComparison(ACompareableExpression node, Object expr1, Operator op, Object expr2) {
        if (expr1 instanceof String && ((String) expr1).contains(OCL.SIZE)){
            // it's one of the size rules
            return applySize(node, (IntConstant) expr2, op);
        } else if (expr1 instanceof String && OCL.NULL.equals(expr2)) {
            if (op.equals(Operator.EQ)){
                // rule10 (= null is equal to isEmpty)
                return applyIsEmpty(getPostfixExpression(node), (String) expr1);
            } else if (op.equals(Operator.NEQ)){
                // rule11 (<> null is equal to notEmpty)
                return applyNotEmpty(getPostfixExpression(node), (String) expr1);
            } else {
                assert false; //shouldn't happen
                return null;
            }
        } else {
            // rule12
            return applyExprOpExpr(node, expr1, op, expr2);
        }
    }

    private Condition applySize(ACompareableExpression node, IntConstant expr2, Operator op) {
        int n = expr2.getConstant();
        if (op.equals(Operator.GTEQ)){
            // rule22
            return applySize(node, n);
        } else if (op.equals(Operator.GT)){
            // rule23
            return applySize(node, n+1);
        } else if (op.equals(Operator.EQ)){
            // rule24
            return new AndCondition(applySize(node, n), negate(applySize(node, n+1)));
        } else if (op.equals(Operator.LTEQ)){
            // rule25
            return negate(applySize(node, n+1));
        } else if (op.equals(Operator.LT)){
            // rule26
            return negate(applySize(node, n));
        } else if (op.equals(Operator.NEQ)){
            // rule27
            return negate(new AndCondition(applySize(node, n), negate(applySize(node, n+1))));
        } else {
            assert false; //shouldn't happen
            return null;
        }
    }

    /**
     * Given ACompareableExpression, return its APostfixExpression
     * TODO: this should be fixed in an initial parse stroke
     */
    private APostfixExpression getPostfixExpression(ACompareableExpression node) {
        return ((APostfixExpression) ((AUnaryExpression) ((AMultiplicativeExpression) ((AAdditiveExpression) node
                .getAdditiveExpression())
                .getMultiplicativeExpression())
                .getUnaryExpression())
                .getPostfixExpression());
    }

    /**
     * Given the expr1, and constant n
     * Apply transformation rule22
     */
    private LaxCondition applySize(ACompareableExpression node, int n) {
        APostfixExpression propertyInvocation = getPostfixExpression(node);

        // set expr1, this removes the ->size() part
        String expr1 = propertyInvocation.getPrimaryExpression().toString();
        for (PPropertyInvocation invocation : propertyInvocation.getPropertyInvocation()) {
            String inv = invocation.toString();
            if (!inv.contains(OCL.SIZE)) {
                expr1 = expr1.concat(invocation.toString());
            }
        }
        expr1 = expr1.replaceAll(" ", "");
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
            Condition trs = tr_S(propertyInvocation, expr1, graphBuilder.cloneGraph(var));
            con = new AndCondition(con, trs);

            // merge the vars
            vars = graphBuilder.mergeGraphs(vars, var);
        }

        // create the != edges
        PlainGraph neq = graphBuilder.cloneGraph(vars);
        for (int i = 0; i < n-1; i++) {
            for (int j = i + 1; j < n; j++) {
                graphBuilder.addEdge(neq, varNames.get(i), NEQ ,varNames.get(j));
            }
        }
        LaxCondition neqCon = new LaxCondition(Quantifier.EXISTS, neq);
        return new LaxCondition(Quantifier.EXISTS, vars, new AndCondition(neqCon, con));
    }

    private LaxCondition applyExprOpExpr(Node node, Object expr1, Operator op, Object expr2) {
        // TODO support for set equallity (forall rule)
        TypeNode t1 = determineType(node, expr1);
        TypeNode t2 = determineType(node, expr2);

        // t1 and t2 should be the same type
        assert t1.equals(t2);

        PlainGraph varX = graphBuilder.createGraph();
        String x = graphBuilder.addNode(varX, t1.text());
        Condition trv1 = tr_N(node, expr1, graphBuilder.cloneGraph(varX));

        PlainGraph varY = graphBuilder.createGraph();
        String y = graphBuilder.addNode(varY, t2.text());
        Condition trv2 = tr_N(node, expr2, graphBuilder.cloneGraph(varY));

        // merge the graphs and create the operator
        PlainGraph vars = graphBuilder.mergeGraphs(varX, varY);
        if (OCL.PRIMITIVE_TYPES.contains(t1.text())) {
            // if its a primitive type, then you need a production rule
            graphBuilder.addProductionRule(vars, x, t1.text(), op, y);
        } else {
            // else it is an = or !=  edge between them
            if (op.equals(Operator.EQ)) {
                graphBuilder.addEdge(vars, x, EQ, y);
            } else {
                graphBuilder.addEdge(vars, x, NEQ, y);
            }
        }

        AndCondition trv = new AndCondition(trv1, trv2);
        return new LaxCondition(Quantifier.EXISTS, vars, trv);
    }

    private Condition applyExprOpExprSet(Node node, Object expr1, Operator op, Object expr2) {
        Condition n1 = applyOneSideSet(node, expr1, op, expr2);
        Condition n2 = applyOneSideSet(node, expr2, op, expr1);
        return new LaxCondition(Quantifier.EXISTS, graphBuilder.createGraph(), new AndCondition(n1, n2));
    }

    private Condition applyOneSideSet(Node node, Object expr1, Operator op, Object expr2) {
        TypeNode t1 = determineType(node, expr1);
        TypeNode t2 = determineType(node, expr2);

        // t1 and t2 should be the same type
        assert t1.equals(t2);

        PlainGraph varX = graphBuilder.createGraph();
        String x = graphBuilder.addNode(varX, t1.text());
        Condition trv1 = tr_N(node, expr1, graphBuilder.cloneGraph(varX));

        PlainGraph varY = graphBuilder.createGraph();
        String y = graphBuilder.addNode(varY, t2.text());
        Condition trv2 = tr_N(node, expr2, graphBuilder.cloneGraph(varY));

        // merge the graphs and create the operator
        PlainGraph vars = graphBuilder.mergeGraphs(graphBuilder.cloneGraph(varX), graphBuilder.cloneGraph(varY));
        if (OCL.PRIMITIVE_TYPES.contains(t1.text())) {
            // if its a primitive type, then you need a production rule
            graphBuilder.addProductionRule(vars, x, t1.text(), op, y);
        } else {
            // else it is an = or !=  edge between them
            if (op.equals(Operator.EQ)) {
                graphBuilder.addEdge(vars, x, EQ, y);
            } else {
                graphBuilder.addEdge(vars, x, NEQ, y);
            }
        }

        return new LaxCondition(Quantifier.FORALL, varX, new AndCondition(new LaxCondition(Quantifier.EXISTS, vars, trv2), trv1));
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
    public void outAAdditiveExpression(AAdditiveExpression node) {
        if (!node.getAddition().isEmpty()) {
            resetOut(node);
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
                // rule16 || rule18
                resetOut(node, applyIncludes(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.EXCLUDES_ALL.equals(operation) || OCL.EXCLUDES.equals(operation)) {
                //rule17 || rule19
                resetOut(node, applyExcludes(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.NOT_EMPTY.equals(operation)) {
                // rule20
                resetOut(node, applyNotEmpty(node, expr1));
            } else if (OCL.IS_EMPTY.equals(operation)) {
                // rule21
                resetOut(node, applyIsEmpty(node, expr1));
            } else if (OCL.OCL_IS_KIND_OF.equals(operation)) {
                //rule30
                resetOut(node, applyOclIsKindOf(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.OCL_IS_TYPE_OF.equals(operation)) {
                // rule31
                resetOut(node, applyOclIsTypeOf(node, expr1, getExpr2FromPropertyCall(propertyCall)));
            } else if (OCL.EXISTS.equals(operation)) {
                // rule13
                resetOut(node, applyExists(node, expr1, propertyCall));
            } else if (OCL.FORALL.equals(operation)) {
                // rule14 || rule15
                resetOut(node, applyForall(node, expr1, propertyCall));
            } else if (OCL.IS_UNIQUE.equals(operation)) {
                // rule29
                resetOut(node, applyIsUnique(node, expr1, (String) getOut(node)));
            } else if (OCL.SIZE.equals(operation) || OCL.MIN.equals(operation) || OCL.MAX.equals(operation)) {
                // operation SIZE has to be compared with a constant, the constant is not available at this point in the tree
                // the same for MIN and MAX
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

    private LaxCondition applyIncludes(APostfixExpression node, String expr1, String expr2) {
        PlainGraph var = graphBuilder.createVar(determineType(node, expr1).text());

        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(var));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(var));

        Condition condition = new ImpliesCondition(trs2, trs1);
        return new LaxCondition(Quantifier.FORALL, var, condition);
    }

    private LaxCondition applyExcludes(APostfixExpression node, String expr1, String expr2) {
        PlainGraph var = graphBuilder.createVar(determineType(node, expr1).text());

        Condition trs1 = tr_S(node, expr1, graphBuilder.cloneGraph(var));
        Condition trs2 = tr_S(node, expr2, graphBuilder.cloneGraph(var));

        Condition condition = new ImpliesCondition(trs2, negate(trs1, false, true));
        return new LaxCondition(Quantifier.FORALL, var, condition);
    }

    private LaxCondition applyNotEmpty(APostfixExpression node, String expr) {
        PlainGraph var = graphBuilder.createVar(determineType(node, expr).text());

        Condition trs = tr_S(node, expr, graphBuilder.cloneGraph(var));
        return new LaxCondition(Quantifier.EXISTS, var, trs);
    }

    private LaxCondition applyIsEmpty(APostfixExpression node, String expr) {
        PlainGraph var = graphBuilder.createGraph();
        String varn = graphBuilder.addNode(var, determineType(node, expr).text());

        Condition trs = tr_S(node, expr, graphBuilder.cloneGraph(var));
        graphBuilder.addEdge(var, varn, String.format("%s:", NOT), varn);
        return new LaxCondition(Quantifier.EXISTS, var, trs);
    }

    private LaxCondition applyOclIsKindOf(Node node, String expr, String T) {
        return applyTypeCheck(node, expr, T, false);
    }

    private LaxCondition applyOclIsTypeOf(Node node, String expr, String T) {
        return applyTypeCheck(node, expr, T, true);
    }

    private LaxCondition applyTypeCheck(Node node, String expr, String T, boolean sharpType) {
        // create v:T`
        PlainGraph var = graphBuilder.createGraph();
        String v = graphBuilder.addNode(var, determineType(node, expr).text());
        // connect it with expr1
        Condition trn = tr_N(node, expr, graphBuilder.cloneGraph(var));

        String typeCheck;
        if (sharpType){
            // make sure that v OclIsTypeOf T
            typeCheck = graphBuilder.addNode(var, String.format("%s%s", SHARP_TYPE, T));
        } else {
            // make sure that v OclIsKindOf T
            typeCheck = graphBuilder.addNode(var, String.format("%s", T));
        }
        graphBuilder.addEdge(var, v, EQ, typeCheck);

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
        Condition neq1 = applyExprOpExpr(node, var1Name, Operator.NEQ, var2Name);
        Condition neq2 = applyExprOpExpr(node, String.format("%s.%s", var1Name, attr), Operator.NEQ, String.format("%s.%s", var2Name, attr));
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
            // apply rule15
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
    private TypeNode determineType(Node node, Object expr) {
        if (expr instanceof Constant) {
            return typeGraph.getNode(((Constant) expr).getTypeString());
        }

        List<String> split = splitExpr((String) expr);

        String curType = graphBuilder.getVariableType(split.get(0));
        if (curType == null){
            if (split.size() > 1 && split.get(1).contains(OCL.ALL_INSTANCES)) {
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
        for (String path : split) {
            String element = null;
            if (path.contains("(")) {
                String[] temp = path.split("([()])+");
                path = temp[0];
                if (temp.length > 1) {
                    element = temp[1];
                }
            }
            // check if the path is an OCL cast, because that will change the way we have to follow the edges
            if (path.equals(OCL.OCL_AS_TYPE)) {
                type = typeGraph.getNode(String.format("%s:%s", TYPE, element));
            } else if (OCL.MINUS.equals(path)) {
                // the important type is at the front side of the equation
                assert type != null;
                TypeNode t2 = determineType(node, ((String) expr).split(OCL.MINUS)[1]);
                if (!t2.equals(type)) {
                    type = determineLowestType(type, t2);
                }
                return type;
            } else if (OCL.SET_OPERATIONS_SUPER_TYPE.contains(path)) {
                assert type != null;
                TypeNode t2 = determineType(node, element);
                if (!t2.equals(type)) {
                    type = determineLowestType(type, t2);
                }
            } else //noinspection StatementWithEmptyBody
                if (OCL.SET_OPERATIONS.contains(path)) {
                //ignore
            } else {
                // follow the edges to the final type node
                List<TypeEdge> typeEdges = getTypeNodeOfAttribute(type, path);

                if (typeEdges.isEmpty()) {
                    // the type does not have the path edge, does one of its super types contain the path edge?
                    assert type != null;
                    for (TypeNode superType : Collections.unmodifiableSet(type.getSupertypes())) {
                        typeEdges = getTypeNodeOfAttribute(superType, path);
                        if (!typeEdges.isEmpty()) {
                            break;
                        }
                    }
                    // if the edge does not exist in the type graph and neither in the supertypes, the given OCL expression is not correct
                    if (typeEdges.isEmpty()) {
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

    private ArrayList<String> splitExpr(String expr) {
        ArrayList<String> split = new ArrayList<>();
        String word = "";
        int inBracket = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);

            if (c == '(') {
                // 1 level deeper
                inBracket++;
            } else if (c == ')') {
                // 1 level up
                inBracket--;
            } else if (inBracket == 0 &&
                    (c == '.' || (c == '-'))) {
                // if we are not nested and we see a dot(.) or minus(-) add the word to split and continue
                split.add(word);
                word = "";

                if (c == '-') {
                    if (expr.charAt(i+1) == '>') {
                        //skip the next character in case the minus appears to be an arrow (->)
                        i++;
                    } else {
                        // minus is also an operator that should be part of the split
                        split.add(OCL.MINUS);
                    }
                }
                continue;
            }
            // keep creating the word
            word = word.concat(String.valueOf(c));
        }
        // add the final word
        split.add(word);
        return split;
    }

    /**
     * Determine the lowest equal parent if there is nothing common, then Object is the only one
     */
    private TypeNode determineLowestType(TypeNode type1, TypeNode type2) {
        Set<TypeNode> s1 = type1.getSupertypes();
        Set<TypeNode> s2 = type2.getSupertypes();
        s1.retainAll(s2);

        TypeNode parent = null;
        for (TypeNode t : s1) {
            Set<TypeNode> st = t.getSubtypes();
            st.retainAll(s1);
            if (st.size() == 1) {
                parent = t;
            }
        }
        return parent;
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
    private Condition tr_N(Node node, Object exprO, PlainGraph graph) {
        if (exprO instanceof Constant) {
            //rule34
            String x = graphBuilder.getVarNameOfNoden0(graph);
            PlainGraph var = graphBuilder.createVar(x, ((Constant) exprO).getGrooveString());
            return new LaxCondition(Quantifier.EXISTS, var);
        }
        String expr = (String) exprO;
        if (expr.contains(OCL.MIN)) {
            // rule37
            return applyMinMax(node, expr, graph, Operator.LTEQ);
        } else if (expr. contains(OCL.MAX)) {
            // rule38
            return applyMinMax(node, expr, graph, Operator.GTEQ);
        } else if (expr.contains(".")) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split("\\.")));
            String role = split.remove(split.size() - 1);
            expr = StringUtils.join(split, ".");

            if (role.contains(OCL.OCL_AS_TYPE)) {
                // rule35
                return applyOclAsType(node, expr, graph);
            } else {
                //rule36
                return applyNavigationRole(node, expr, role, graph);
            }
        } else {
            // rule33
            String vp = graphBuilder.getVarNameOfNoden0(graph);
            graphBuilder.addNode(graph, expr, graphBuilder.getVariableType(vp));
            graphBuilder.addEdge(graph, vp, EQUIV, expr);

            return new LaxCondition(Quantifier.EXISTS, graph);
        }
    }

    private LaxCondition applyMinMax(Node node, String expr, PlainGraph varX, Operator minMaxOp) {
        // determine type and name of first variable
        Tuple2<Tuple2<String, TypeNode>, Tuple2<String, TypeNode>> exprAttrType = determineTypeAndAttribute(node, expr);
        String attrType = exprAttrType.getSecond().getSecond().text();
        String attr = exprAttrType.getSecond().getFirst();
        String nameX = graphBuilder.getVarNameOfNoden0(varX);

        expr = exprAttrType.getFirst().getFirst();

        // create v1:T
        PlainGraph v1 = graphBuilder.createGraph();
        String v1Name = graphBuilder.addNode(v1, exprAttrType.getFirst().getSecond().text());

        Condition trn1 = tr_N(node, expr, graphBuilder.cloneGraph(v1));

        // connect v1:T with attr x:T
        v1 = graphBuilder.mergeGraphs(v1, graphBuilder.cloneGraph(varX));
        graphBuilder.addEdge(v1, v1Name, exprAttrType.getSecond().getFirst(), nameX);

        //create v2:T
        PlainGraph v2 = graphBuilder.createGraph();
        String v2Name = graphBuilder.addNode(v2, exprAttrType.getFirst().getSecond().text());

        Condition trn2 = tr_N(node, expr, graphBuilder.cloneGraph(v2));

        // create attr v3:T
        PlainGraph v3 = graphBuilder.createGraph();
        String v3Name = graphBuilder.addNode(v3, attrType);

        // create the production
        PlainGraph minMax = graphBuilder.mergeGraphs(graphBuilder.cloneGraph(varX), graphBuilder.cloneGraph(v3));
        graphBuilder.addProductionRule(minMax, nameX, attrType, minMaxOp, v3Name);

        // connect v2:T with v3:T
        v2 = graphBuilder.mergeGraphs(v2, v3);
        graphBuilder.addEdge(v2, v2Name, attr, v3Name);

        LaxCondition tre = new LaxCondition(Quantifier.EXISTS, minMax);
        LaxCondition forAll = new LaxCondition(Quantifier.FORALL, v2, new ImpliesCondition(trn2, tre));
        return new LaxCondition(Quantifier.EXISTS, v1, new AndCondition(trn1, forAll));
    }

    private LaxCondition applyOclAsType(Node node, String expr, PlainGraph graph) {
        //create v:T`
        PlainGraph var = graphBuilder.createGraph();
        String v = graphBuilder.addNode(var, determineType(node, expr).text());

        Condition trn = tr_N(node, expr, graphBuilder.cloneGraph(var));

        // create expr.oclAsType()
        String vt = graphBuilder.getVarNameOfNoden0(graph);
        var = graphBuilder.mergeGraphs(graph, var);
        graphBuilder.addEdge(var, v, EQ, vt);

        return new LaxCondition(Quantifier.EXISTS, var, trn);
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

    private Condition tr_S(APostfixExpression node, String expr, PlainGraph graph) {
        if (expr.contains(OCL.ARROW)) {
            LinkedList<PPropertyInvocation> propertyInvocation = node.getPropertyInvocation();
            PPropertyCall propertyCall = ((ACollectionPropertyInvocation) propertyInvocation.get(1)).getPropertyCall();

            String expr1 = (node.getPrimaryExpression().toString() + propertyInvocation.get(0).toString()).replace(" ", "");
            String expr2 = ((APropertyCallParameters) ((APropertyCall) propertyCall)
                    .getPropertyCallParameters())
                    .getActualParameterList()
                    .toString().replaceAll(" ", "");
            String operation = ((APropertyCall) propertyCall).getPathName().toString().trim();

            // determine which translation rule to apply
            switch (operation) {
                case OCL.UNION:
                case OCL.INCLUDING:
                    // rule42 || rule46
                    return applyUnion(node, expr1, expr2, graph);
                case OCL.INTERSECTION:
                    // rule43
                    return applyIntersection(node, expr1, expr2, graph);
                case OCL.EXCLUDING:
                    // rule47
                    return applyMinus(node, expr1, expr2, graph);
                case OCL.SYMMETRICDIFFERENCE:
                    // rule45
                    Condition union = applyUnion(node, expr1, expr2, graph);
                    Condition intersect = applyIntersection(node, expr1, expr2, graph);
                    return new AndCondition(union, negate(intersect));
                case OCL.SELECT:
                    // rule48
                    return applySelect(node, expr1, (APropertyCall) propertyCall, graph);
                case OCL.REJECT:
                    // rule49
                    return applyReject(node, expr1, (APropertyCall) propertyCall, graph);
                case OCL.SELECTBYKIND:
                    // rule50
                    return applySelectType(node, expr1, expr2, graph, false);
                case OCL.SELECTBYTYPE:
                    // rule51
                    return applySelectType(node, expr1, expr2, graph, true);
            }
        } else if (expr.contains(OCL.MINUS)) {
            String[] split = expr.split(OCL.MINUS);
            return applyMinus(node, split[0], split[1], graph);
        } else if (expr.contains(OCL.DOT)) {
            List<String> split = new ArrayList<>(Arrays.asList(expr.split("\\.")));
            String role = split.remove(split.size() - 1);
            expr = StringUtils.join(split, OCL.DOT);

            if (role.contains(OCL.ALL_INSTANCES)){
                //rule40
                return new LaxCondition(Quantifier.EXISTS, graph);
            } else if (role.contains(OCL.OCL_AS_SET)) {
                //rule52
                return tr_N(node, expr, graph);
            } else {
                //rule39
                return applyNavigationRole(node, expr, role, graph);
            }
        }
        assert false; //shouldn't happen
        return null;
    }

    private Condition tr_S_special(APostfixExpression node, String expr, PlainGraph graph) {
        TypeNode t = determineType(node, expr);
        String objectName = graphBuilder.getVarNameOfNoden0(graph);

        // create the real type of expr1
        PlainGraph var = graphBuilder.createGraph();
        String varName = graphBuilder.addNode(var, t.text());

        Condition trs = tr_S(node, expr, graphBuilder.cloneGraph(var));

        // real type of expr 1 should be equal to varname
        var = graphBuilder.mergeGraphs(graphBuilder.cloneGraph(graph), var);
        graphBuilder.addEdge(var, objectName, EQ, varName);

        return new LaxCondition(Quantifier.EXISTS, var, trs);
    }

    private Condition applyUnion(APostfixExpression node, String expr1, String expr2, PlainGraph graph) {
        Condition l1 = tr_S_special(node, expr1, graph);
        Condition l2 = tr_S_special(node, expr2, graph);
        return new OrCondition(l1, l2);
    }

    private Condition applyIntersection(APostfixExpression node, String expr1, String expr2, PlainGraph graph) {
        Condition trs1 = tr_S_special(node, expr1, graph);
        Condition trs2 = tr_S_special(node, expr2, graph);
        return new AndCondition(trs1, trs2);
    }

    private Condition applyMinus(APostfixExpression node, String expr1, String expr2, PlainGraph graph) {
        Condition trs1 = tr_S_special(node, expr1, graph);
        Condition trs2 = tr_S_special(node, expr2, graph);
        return new AndCondition(trs1, negate(trs2));
    }

    private Condition applySelect(APostfixExpression node, String expr1, APropertyCall propertyCall, PlainGraph graph) {
        Condition trs1 = tr_S(node, expr1, graph);

        // expr2 is already translated
        Condition expr2 = (Condition) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());
        expr2 = graphBuilder.cloneAndRenameCondition(expr2);

        // rename v to vp in expr2
        String vp = graphBuilder.getVarNameOfNoden0(graph);
        String v = ((AConcreteDeclarator) ((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getDeclarator()).getActualParameterList().toString().trim();
        graphBuilder.renameVar(expr2, v, vp);

        return new AndCondition(trs1, expr2);
    }

    private Condition applyReject(APostfixExpression node, String expr1, APropertyCall propertyCall, PlainGraph graph) {
        Condition trs1 = tr_S(node, expr1, graph);

        // expr2 is already translated
        Condition expr2 = (Condition) getOut(((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getActualParameterList());
        expr2 = negate(graphBuilder.cloneAndRenameCondition(expr2));

        // rename v to vp in expr2
        String vp = graphBuilder.getVarNameOfNoden0(graph);
        String v = ((AConcreteDeclarator) ((APropertyCallParameters) propertyCall.getPropertyCallParameters()).getDeclarator()).getActualParameterList().toString().trim();
        graphBuilder.renameVar(expr2, v, vp);

        return new AndCondition(trs1, expr2);
    }

    private Condition applySelectType(APostfixExpression node, String expr1, String expr2, PlainGraph graph, boolean sharpType) {
        PlainGraph type = graphBuilder.cloneGraph(graph);
        String v = graphBuilder.getVarNameOfNoden0(type);
        String typeCheck;
        if (sharpType){
            // make sure that v OclIsTypeOf T
            typeCheck = graphBuilder.addNode(type, String.format("%s%s", SHARP_TYPE, expr2));
        } else {
            // make sure that v OclIsKindOf T
            typeCheck = graphBuilder.addNode(type, String.format("%s", expr2));
        }
        graphBuilder.addEdge(type, v, EQ, typeCheck);
        LaxCondition typeCon = new LaxCondition(Quantifier.EXISTS, type);

        Condition trs = tr_S(node, expr1, graph);

        return new AndCondition(typeCon, trs);
    }

    public Map<LaxCondition, GraphBuilder> getResults() {
        return results;
    }
}
