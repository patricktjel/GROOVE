package groove.ocl.lax;

import java.util.Objects;

public class Variable implements Expression{
    private final String className;
    private String variableName;

    protected Variable(String variableName, String className) {
        this.variableName = variableName;
        this.className = className;
    }

    public final String getClassName() {
        return className;
    }

    public final String getVariableName() {
        return variableName;
    }

    @Override
    public void renameVar(String o, String n) {
        if (variableName.equals(o)) {
            this.variableName = n;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(className, variable.className) &&
                Objects.equals(variableName, variable.variableName);
    }

    @Override
    public String toString() {
        return String.format("%s:%s",variableName, className);
    }
}
