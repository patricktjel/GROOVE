package groove.ocl.lax.condition;

public class AndExpression implements Condition {
    private Condition expr1;
    private Condition expr2;

    public AndExpression(Condition expr1, Condition expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public Condition getExpr1() {
        return expr1;
    }

    public Condition getExpr2() {
        return expr2;
    }

//    public void renameVar(String o, String n) {
//        expr1.renameVar(o, n);
//        expr2.renameVar(o, n);
//    }
//
//    @Override
//    public boolean simplify() {
//        return expr1.simplify() || expr2.simplify();
//    }

    @Override
    public String toString() {
        return String.format("%s \u2227 %s", expr1, expr2);
    }
}
