package groove.ocl.parser;

import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.node.*;
import groove.ocl.Quantifier;
import groove.ocl.lax.Condition;
import groove.ocl.lax.LaxCondition;
import groove.ocl.lax.Variable;
import groove.util.Log;

import java.util.Hashtable;
import java.util.logging.Logger;

public class TranslateOCLToLax extends DepthFirstAdapter {

    private final static Logger LOGGER = Log.getLogger(TranslateOCLToLax.class.getName());

    private Hashtable<String, String> types;

    public TranslateOCLToLax() {
        this.types = new Hashtable<>();
    }

    @Override
    public void outStart(Start node) {
        // nothing yet, but the start node doesn't have a parent
    }

    @Override
    public void outAConstraint(AConstraint node) {
        String var = getOut(node.getContextDeclaration()).toString();
        String clazz = types.get(var);

        Variable variable = new Variable(clazz, var);
        Condition condition = new Condition("true");
        LaxCondition laxCondition = new LaxCondition(Quantifier.FORALL, variable, condition);
        System.out.println(laxCondition);
    }

    /**
     * The type of the classifier will be set in types
     * The variable name will be given through via this.out
     */
    @Override
    public void outAClassifierContextKind(AClassifierContextKind node) {
        String clazz, var;
        // the type will be set in types
        if (node.getClassifierType() != null) {
            // Equals to rule2
            // the context class is given a variable (a:Person)
            clazz = getOut(node.getClassifierType()).toString();
            var = getOut(node.getName()).toString();
        } else {
            // Equals to rule1
            // the context class is given the variable self (self:Person)
            clazz = getOut(node.getName()).toString();
            var = "self";
        }
        types.put(var, clazz);
        resetOut(node, var);
    }

    @Override
    public void outAName(AName node) {
        resetOut(node, node.getIdentifier());
    }

    @Override
    public void outANumberLiteral(ANumberLiteral node) {
        resetOut(node.parent(), node.getNumberLiteral());
    }

    @Override
    public void defaultOut(Node node) {
        setOut(node.parent(), getOut(node));
    }

    /**
     * Helper method that resets the current node and
     * sets the parent value such that it is possible to give that value through if necessary
     */
    public void resetOut(Node node, Object o) {
        setOut(node , o);
        setOut(node.parent(), o);
    }
}
