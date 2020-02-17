package groove.ocl.lax;

public class Variable extends Expression{
    private String className;
    private String variableName;

    public Variable(String className, String variableName) {
        this.className = className;
        this.variableName = variableName;
    }

    @Override
    public String toString() {
        return String.format("%s:%s",variableName, className);
    }
}
