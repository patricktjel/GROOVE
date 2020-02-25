package groove.ocl.lax;

public class AttributedGraph implements Expression {
    private final Variable variable;

    private final Variable attr1;
    private final Operator operator;
    private final Expression attr2;

    public AttributedGraph(Variable variable, Variable attr1, Operator operator, Expression attr2) {
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

    public Expression getAttr2() {
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
