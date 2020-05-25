package groove.ocl.lax.graph.constants;

public class IntConstant implements Constant<Integer> {

    private final int constant;

    public IntConstant(int constant) {
        this.constant = constant;
    }

    @Override
    public final Integer getConstant() {
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

    @Override
    public String getTypeString() {
        return "type:int";
    }
}
