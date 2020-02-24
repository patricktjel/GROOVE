package groove.ocl.lax.constants;

public class IntConstant extends Constant {

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
}
