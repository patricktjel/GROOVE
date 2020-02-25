package groove.ocl.lax.graph;

import groove.ocl.lax.Operator;

public class AttributedGraph implements Graph {
    private final Variable variable;

    private final Variable attr1;
    private final Operator operator;
    private final Graph attr2;

    public AttributedGraph(Variable variable, Variable attr1, Operator operator, Graph attr2) {
        this.variable = variable;
        this.attr1 = attr1;
        this.operator = operator;
        this.attr2 = attr2;
    }

    public Variable getVariable() {
        return variable;
    }

    public Variable getAttr1() {
        return attr1;
    }

    public Operator getOperator() {
        return operator;
    }

    public Graph getAttr2() {
        return attr2;
    }

    @Override
    public void renameVar(String o, String n) {
        variable.renameVar(o, n);
    }

    @Override
    public String toString() {
        return String.format("(%s | %s %s %s)", variable, attr1, operator, attr2);
    }
}
