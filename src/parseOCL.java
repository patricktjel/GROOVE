import de.tuberlin.cs.cis.ocl.parser.analysis.DepthFirstAdapter;
import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.lexer.LexerException;
import de.tuberlin.cs.cis.ocl.parser.node.Node;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import de.tuberlin.cs.cis.ocl.parser.parser.ParserException;
import groove.graph.plain.PlainGraph;
import groove.ocl.GrammarStorage;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.LaxSimplifier;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;
import groove.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Map;
import java.util.logging.Logger;

public class parseOCL {

    private final static Logger LOGGER = Log.getLogger(parseOCL.class.getName());

    private static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\test.gps";

    static public void main(String[] args) throws ParserException, IOException, LexerException {
        String ocl =
                "context Person inv: self.age >= 18 " +
                "context Person inv: self.c.age >= 18"
                ;
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();

        // set GROOVE project
        GrammarStorage grammarStorage = new GrammarStorage(GRAPH_LOCATION);

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

            PlainGraph graph = graphBuilder.laxToGraph(condition);
            grammarStorage.saveGraph(graph);
        }

//        parseTree.apply(new TreePrinter(new PrintWriter(System.out)));
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
