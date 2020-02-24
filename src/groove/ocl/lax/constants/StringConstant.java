package groove.ocl.lax.constants;

public class StringConstant extends Constant {

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
}
