package groove.ocl.lax.graph.constants;

public class RealConstant implements Constant<Double> {

    private final double constant;

    public RealConstant(double constant) {
        this.constant = constant;
    }

    @Override
    public final Double getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return Double.toString(constant);
    }

    @Override
    public String getGrooveString() {
        return String.format("real:%s", getConstant());
    }

    @Override
    public String getTypeString() {
        return "type:real";
    }
}
