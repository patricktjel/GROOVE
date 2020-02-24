package groove.ocl.lax;

public class Condition implements Expression {
    private String condition;

    public Condition(String condition) {
        this.condition = condition;
    }

    @Override
    public void renameVar(String o, String n) {
        this.condition = condition.replace(o, n);
    }

    @Override
    public String toString() {
        return condition;
    }
}
