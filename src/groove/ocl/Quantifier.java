package groove.ocl;

import groove.io.Util;

public enum Quantifier {
    /** Universally quantified pattern. */
    FORALL("Universal", Util.FORALL),
    /** Existentially quantified pattern. */
    EXISTS("Existential", Util.EXISTS);

    private String name;

    // Unicode hex strin
    private String symbol;

    private Quantifier(String name, char symbol) {
        this(name, "" + symbol);
    }

    private Quantifier(String name, String symbol) {
        this.name = name;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
