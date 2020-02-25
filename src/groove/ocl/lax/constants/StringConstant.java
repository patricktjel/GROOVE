package groove.ocl.lax.constants;

import groove.ocl.lax.Expression;

public class StringConstant implements Constant, Expression {

    private final String constant;

    public StringConstant(String constant) {
        this.constant = constant;
    }

    public final String getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return constant;
    }

    @Override
    public void renameVar(String o, String n) {
        // ignore
    }

    @Override
    public String getGrooveString() {
        return String.format("string:\"%s\"", getConstant());
    }
}
