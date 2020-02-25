package groove.ocl.lax;

import groove.graph.Label;
import groove.io.Util;

public enum Quantifier implements Comparable<Quantifier> {
    /** Existentially quantified pattern. */
    EXISTS(Util.EXISTS),
    /** Universally quantified pattern. */
    FORALL(Util.FORALL);

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
