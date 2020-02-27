package groove.ocl.lax.condition;

import java.util.Map;

public interface Condition {

    /**
     * Rename variables in the graph from the old node o to the new node n
     * @param o     The old Node name
     * @param n     The new Node name
     */
    void renameVar(String o, String n);

    /**
     * Recursive call to simplify the Lax Condition by applying equivalence rules.
     * @return      A list with equivalences according to E3
     */
    Map<String, String> simplify();
}
