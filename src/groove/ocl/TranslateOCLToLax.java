package groove.ocl;

import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.node.*;
import groove.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class TranslateOCLToLax extends DepthFirstAdapter {

    private final static Logger LOGGER = Log.getLogger(TranslateOCLToLax.class.getName());

    private Map<String, String> varTypeMap = new HashMap<>();

//    @Override
//    public void caseAClassifierContextKind(AClassifierContextKind node) {
//        String var, C;
//        if (node.getClassifierType() == null) {
//            // invariant rule 1: ‘context‘ C ‘inv:‘ exp
//            var = "self";
//            C = node.getName().toString();
//            LOGGER.info(String.format("Applied rule 1: C=%s", C));
//        } else {
//            // invariant rule 2: ‘context‘ var:C ‘inv:‘ exp
//            var = node.getName().toString();
//            C = ((AClassifierType)node.getClassifierType()).getName().toString();
//            LOGGER.info(String.format("Applied rule 2: var=%s, C=%s", var, C));
//        }
//        varTypeMap.put(var, C);
//    }
//
//    @Override
//    public void defaultCase(Node node) {
//        LOGGER.info(node.toString() + " "+ node.getClass());
//    }

//    @Override
//    public void defaultOut(Node node) {
//        LOGGER.info(node.toString() + " "+ node.getClass());
//    }

    @Override
    public void defaultIn(Node node) {
        LOGGER.info(node.toString() + " "+ node.getClass());
    }
}
