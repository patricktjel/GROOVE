package groove.ocl.parser;

import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.node.*;
import groove.grammar.type.TypeEdge;
import groove.grammar.type.TypeGraph;
import groove.grammar.type.TypeNode;
import groove.ocl.GrammarStorage;
import groove.ocl.InvalidOCLException;
import groove.ocl.lax.Quantifier;
import groove.ocl.lax.*;
import groove.ocl.lax.constants.BooleanConstant;
import groove.ocl.lax.constants.Constant;
import groove.ocl.lax.constants.IntConstant;
import groove.ocl.lax.constants.StringConstant;
import groove.util.Log;
import groove.util.Triple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
        Variable variable = (Variable) getOut(node.getContextDeclaration());
        LaxCondition con = (LaxCondition) getOut(node.getContextBodypart().get(0));
        resetOut(node, new LaxCondition(Quantifier.FORALL, variable, con));
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
        resetOut(node, VariableFactory.createVariable(var,clazz));
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

            Triple<String, TypeNode, Variable> e1 = determineTypeAndAttribute(expr1);
            expr1 = e1.one();
            if (expr2 instanceof Constant) {
                // so its rule17
                Variable var = VariableFactory.createVariable(e1.two().text());
                LaxCondition trn = tr_N(expr1, var);
                AttributedGraph attrGraph = new AttributedGraph(var, e1.three(), op, (Expression) expr2);

                // given the values create the right LaxCondition
                resetOut(node, new LaxCondition(Quantifier.EXISTS, var, new AndExpression(trn, attrGraph)));
            } else {
                // its rule 18
                Triple<String, TypeNode, Variable> e2 = determineTypeAndAttribute(expr2.toString());
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
     * Given an expression determine the attribute
     * and the type of the class that the attribute is part of.
     * e.g. "self.age" in the context of Person returns (Person, age)
     * @param expr  The expression
     * @return      A triple with the expression without attribute, the new expressions type,
     *              and the extracted attribute as a variable such that you also know the type of the attr.
     */
    private Triple<String, TypeNode, Variable> determineTypeAndAttribute(String expr) {
        String[] split = expr.split("\\.");
        String attr = split[split.length-1];
        TypeNode typeAttr = determineType(expr);
        expr = String.join(".", Arrays.copyOfRange(split, 0, split.length - 1));
        TypeNode type = determineType(expr);
        return new Triple<>(expr, type, VariableFactory.createVariable(attr, typeAttr.text()));
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
        Variable variable = VariableFactory.getVariable(split.get(0));
        split.remove(variable.getVariableName());
        String curType = variable.getClassName();

        // find the first node
        TypeNode type = typeGraph.nodeSet().stream()
                .filter(n -> n.text().equals(curType))
                .collect(Collectors.toList())
                .get(0);
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
    private LaxCondition tr_N(String expr, Variable var) {
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
                Variable exprVar = VariableFactory.createVariable(exprType.text());
                NavigationVariable navigationVariable = new NavigationVariable(exprVar, role, var);
                LaxCondition trn = tr_N(expr, exprVar);
                con = new LaxCondition(Quantifier.EXISTS, navigationVariable, trn);
            }
        } else {
            // rule22
            con = new LaxCondition(Quantifier.EXISTS, new EquivVariable(expr, var.getVariableName(), var.getClassName()));
        }
        return con;
    }

    public LaxCondition getResult() {
        return result;
    }
}
