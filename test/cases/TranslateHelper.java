package cases;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import groove.graph.plain.PlainGraph;
import groove.ocl.GrammarStorage;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.LaxSimplifier;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;
import groove.util.Log;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TranslateHelper {

    private final static Logger LOGGER = Log.getLogger(TranslateHelper.class.getName());

    public static List<LaxCondition> translateOCLToGraph(String ocl, String graphLocation) throws Exception {
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();
        TranslateOCLToLax translateOCLToLax = new TranslateOCLToLax(grammarStorage.getTypeGraphs());

        LOGGER.info("parsing:         " + ocl);
        parseTree.apply(translateOCLToLax);

        Map<LaxCondition, GraphBuilder> conditions = translateOCLToLax.getResults();
        for (Map.Entry<LaxCondition, GraphBuilder> entry : conditions.entrySet()) {
            LaxCondition condition = entry.getKey();
            GraphBuilder graphBuilder = entry.getValue();

            LaxSimplifier laxSimplifier = new LaxSimplifier(graphBuilder);
            LOGGER.info("Before simplify: " + graphBuilder.conToString(condition));
            laxSimplifier.simplify(condition);
            LOGGER.info("After simplify:  " + graphBuilder.conToString(condition));
        }

        return new ArrayList<>(conditions.keySet());
    }

    public static void createGraph(LaxCondition condition, GraphBuilder graphBuilder, String graphLocation){
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        PlainGraph graph = graphBuilder.laxToGraph(condition);
        grammarStorage.saveGraph(graph);

    }
}
