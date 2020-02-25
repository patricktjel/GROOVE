package groove.ocl.lax.constants;

import groove.ocl.lax.Expression;

public class IntConstant implements Constant, Expression {

    private final int constant;

    public IntConstant(int constant) {
        this.constant = constant;
    }

    public final int getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return Integer.toString(constant);
    }

    @Override
    public void renameVar(String o, String n) {
        //ignore
    }

    @Override
    public String getGrooveString() {
        return String.format("int:%s", getConstant());
    }
}
