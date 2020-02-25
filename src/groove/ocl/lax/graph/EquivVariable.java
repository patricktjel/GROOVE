package groove.ocl.lax.graph;

public class EquivVariable extends Variable {

    private final String var2;

    public EquivVariable(String var1, String var2, String className) {
        super(var1, className);
        this.var2 = var2;
    }

    public String getVar2() {
        return var2;
    }

    @Override
    public String toString() {
        return String.format("%s=%s:%s", super.getVariableName(),var2 , super.getClassName());
    }
}
