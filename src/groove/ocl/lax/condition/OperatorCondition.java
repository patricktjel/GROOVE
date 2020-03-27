package groove.ocl.lax.condition;

public abstract class OperatorCondition implements Condition{
    private Condition expr1;
    private Condition expr2;

    public OperatorCondition(Condition expr1, Condition expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public Condition getExpr1() {
        return expr1;
    }

    public Condition getExpr2() {
        return expr2;
    }

    public void setExpr1(Condition expr1) {
        this.expr1 = expr1;
    }

    public void setExpr2(Condition expr2) {
        this.expr2 = expr2;
    }
}
