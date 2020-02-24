package groove.ocl.lax;

public class Variable extends Expression{
    private final String className;
    private final String variableName;

    protected Variable(String variableName, String className) {
        this.variableName = variableName;
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public String toString() {
        return String.format("%s:%s",variableName, className);
    }
}
