package groove.ocl.lax.condition;

public interface Condition {

    void renameVar(String o, String n);

    boolean simplify();
}
