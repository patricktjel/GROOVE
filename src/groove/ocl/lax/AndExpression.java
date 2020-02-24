package groove.ocl.lax;

public class AndExpression implements Expression {
    private Expression expr1;
    private Expression expr2;

    public AndExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public Expression getExpr1() {
        return expr1;
    }

    public Expression getExpr2() {
        return expr2;
    }

    @Override
    public void renameVar(String o, String n) {
        expr1.renameVar(o, n);
        expr2.renameVar(o, n);
    }

    @Override
    public String toString() {
        return String.format("%s \u2227 %s", expr1, expr2);
    }
}
