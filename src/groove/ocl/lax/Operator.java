package groove.ocl.lax;

public enum Operator {

    GT(">", "gt"),
    GTEQ(">=", "ge"),
    LT("<", "lt"),
    LTEQ("<=", "le"),
    EQ("=", "eq"),
    NEQ("<>", "neq");

    private final String symbol;
    private final String groovename;

    Operator(String symbol, String grooveName) {
        this.symbol = symbol;
        this.groovename = grooveName;
    }

    public final String getSymbol() {
        return symbol;
    }

    public final String getGrooveString(String type) {
        return String.format("%s:%s", type, groovename);
    }

    @Override
    public String toString() {
        return symbol;
    }
}
