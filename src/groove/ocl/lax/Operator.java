package groove.ocl.lax;

public enum Operator {

    GT(">"),
    GTEQ(">="),
    Lt("<"),
    LTEQ("<="),
    EQ("="),
    NEQ("<>");

    private final String symbol;

    Operator(String symbol) {
        this.symbol = symbol;
    }

    public final String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
