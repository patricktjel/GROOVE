package groove.ocl.lax.graph.constants;

public enum BooleanConstant implements Constant {
    TRUE(true),
    FALSE(false);

    private final boolean constant;

    BooleanConstant(boolean constant) {
        this.constant = constant;
    }

    public final boolean getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return Boolean.toString(constant);
    }

    @Override
    public String getGrooveString() {
        return String.format("bool:%s", getConstant());
    }
}
