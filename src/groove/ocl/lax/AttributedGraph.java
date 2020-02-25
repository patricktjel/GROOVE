package groove.ocl.lax;

public class AttributedGraph implements Expression {
    private final Variable variable;

    private final String attr1;
    private final Operator operator;
    private final String attr2;

    public AttributedGraph(Variable variable, String attr1, Operator operator, String attr2) {
        this.variable = variable;
        this.attr1 = attr1;
        this.operator = operator;
        this.attr2 = attr2;
    }

    public Variable getVariable() {
        return variable;
    }

    public String getAttr1() {
        return attr1;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getAttr2() {
        return attr2;
    }

    @Override
    public void renameVar(String o, String n) {
        variable.renameVar(o, n);
    }

    @Override
    public String toString() {
        return String.format("%s | %s %s %s", variable, attr1, operator, attr2);
    }
}
