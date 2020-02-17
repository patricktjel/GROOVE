package groove.ocl.lax;

public class Condition extends Expression {
    private String condition;

    public Condition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return condition;
    }
}
