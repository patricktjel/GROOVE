package groove.ocl.lax.constants;

public class BooleanConstant extends Constant {

    private final boolean constant;

    public BooleanConstant(boolean constant) {
        this.constant = constant;
    }

    public final boolean getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return Boolean.toString(constant);
    }
}
