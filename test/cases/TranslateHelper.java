package cases;

import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.node.Node;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import groove.graph.plain.PlainGraph;
import groove.ocl.GrammarStorage;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.LaxSimplifier;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;
import groove.util.Log;

import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Map;
import java.util.logging.Logger;

public class TranslateHelper {

    private final static Logger LOGGER = Log.getLogger(TranslateHelper.class.getName());

    public static Map<LaxCondition, GraphBuilder> translateOCLToGraph(String ocl, String graphLocation) throws Exception {
        return translateOCLToGraph(ocl, graphLocation, false);
    }

    public static Map<LaxCondition, GraphBuilder> translateOCLToGraph(String ocl, String graphLocation, boolean createGraph) throws Exception {
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();

//        parseTree.apply(new TreePrinter(new PrintWriter(System.out)));

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

            if (createGraph){
                createGraph(condition, graphBuilder, graphLocation);
            }
        }

        return conditions;
    }

    public static void createGraph(LaxCondition condition, GraphBuilder graphBuilder, String graphLocation){
        GrammarStorage grammarStorage = new GrammarStorage(graphLocation);

        PlainGraph graph = graphBuilder.laxToGraph(condition);
        grammarStorage.saveGraph(graph);
    }

    public static class TreePrinter extends DepthFirstAdapter {
        private int depth = 0;
        private PrintWriter out;

        public TreePrinter(PrintWriter out) {
            this.out = out;
        }

        public void defaultCase(Node node) {
            indent();
            out.println(node.toString());
        }

        public void defaultIn(Node node) {
            indent();
            out.println(node.getClass().getSimpleName());
            depth = depth + 1;
        }

        public void defaultOut(Node node) {
            depth = depth - 1;
            out.flush();
        }

        private void indent() {
            for (int i = 0; i < depth; i++) out.write("\t");
        }
    }
}
