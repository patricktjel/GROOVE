package groove.ocl.lax.graph.constants;

public class StringConstant implements Constant<String> {

    private final String constant;

    public StringConstant(String constant) {
        this.constant = constant;
    }

    @Override
    public final String getConstant() {
        return constant;
    }

    @Override
    public String toString() {
        return constant;
    }

    @Override
    public String getGrooveString() {
        return String.format("string:%s", getConstant());
    }

    @Override
    public String getTypeString() {
        return "type:string";
    }
}
