package groove.ocl.lax;

import groove.io.Util;

public enum Quantifier {
    /** Universally quantified pattern. */
    FORALL(Util.FORALL),
    /** Existentially quantified pattern. */
    EXISTS(Util.EXISTS);

    // Unicode hex string
    private String symbol;

    Quantifier(char symbol) {
        this("" + symbol);
    }

    Quantifier(String symbol) {
        this.symbol = symbol;
    }

    public String getGrooveString() {
        return String.format("%s:", this.name().toLowerCase());
    }

    @Override
    public String toString() {
        return symbol;
    }
}
