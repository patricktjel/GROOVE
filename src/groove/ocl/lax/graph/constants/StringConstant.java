package groove.ocl.lax.graph.constants;

public class StringConstant implements Constant {

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
    public String getGrooveString() {
        return String.format("string:\"%s\"", getConstant());
    }
}
