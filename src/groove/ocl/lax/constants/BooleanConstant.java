package groove.ocl.lax.constants;

import groove.ocl.lax.Expression;

public enum BooleanConstant implements Constant, Expression {
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
