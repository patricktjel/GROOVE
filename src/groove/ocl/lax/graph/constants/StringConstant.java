package groove.ocl.lax.graph.constants;

import groove.ocl.lax.graph.Graph;

public class StringConstant implements Constant, Graph {

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
