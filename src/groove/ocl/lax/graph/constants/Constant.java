package groove.ocl.lax.graph.constants;

public interface Constant<T> {
    String getGrooveString();

    String getTypeString();

    T getConstant();
}
