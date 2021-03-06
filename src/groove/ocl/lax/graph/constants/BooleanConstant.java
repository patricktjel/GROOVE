package groove.ocl.lax.graph.constants;

public enum BooleanConstant implements Constant<Boolean> {
    TRUE(true),
    FALSE(false);

    private final boolean constant;

    BooleanConstant(boolean constant) {
        this.constant = constant;
    }

    @Override
    public final Boolean getConstant() {
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

    @Override
    public String getTypeString() {
        return "type:bool";
    }
}
