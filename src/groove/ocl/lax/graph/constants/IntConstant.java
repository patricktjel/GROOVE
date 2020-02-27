package groove.ocl.lax.graph.constants;

public class IntConstant implements Constant {

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
    public String getGrooveString() {
        return String.format("int:%s", getConstant());
    }
}
