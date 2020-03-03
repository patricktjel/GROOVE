package cases;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import groove.graph.plain.PlainGraph;
import groove.ocl.GrammarStorage;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;
import groove.util.Log;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.logging.Logger;

public class TranslateHelper {

    private final static Logger LOGGER = Log.getLogger(TranslateHelper.class.getName());

    public static LaxCondition translateOCLToGraph(String ocl, String graphLocation) throws Exception {
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();
        TranslateOCLToLax translateOCLToLax = new TranslateOCLToLax(grammarStorage.getTypeGraphs());

        LOGGER.info("parsing:         " + ocl);
        parseTree.apply(translateOCLToLax);

        LaxCondition condition = translateOCLToLax.getResult();
        LOGGER.info("Before simplify: " + condition.toString());
        condition.simplify();
        LOGGER.info("After simplify:  " + condition.toString());

        return condition;
    }

    public static void createGraph(LaxCondition condition, String graphLocation){
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        PlainGraph graph = GraphBuilder.laxToGraph(condition);
        grammarStorage.saveGraph(graph);

    }
}
