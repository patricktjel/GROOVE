package groove.ocl.lax.graph.constants;

import groove.ocl.lax.graph.Graph;

public enum BooleanConstant implements Constant, Graph {
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
    public void renameVar(String o, String n) {
        //ignore
    }

    @Override
    public String getGrooveString() {
        return String.format("bool:%s", getConstant());
    }
}
