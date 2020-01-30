package groove.ocl;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.lexer.LexerException;
import de.tuberlin.cs.cis.ocl.parser.node.AOclFile;
import de.tuberlin.cs.cis.ocl.parser.node.PConstraint;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import de.tuberlin.cs.cis.ocl.parser.parser.ParserException;
import groove.util.Log;
import groove.grammar.aspect.AspectGraph;
import groove.grammar.model.ResourceKind;
import groove.graph.GraphRole;
import groove.io.store.SystemStore;

import java.io.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.logging.Logger;

public class OCLParser {
    private final static Logger LOGGER = Log.getLogger(OCLParser.class.getName());

    //todo: use variables instead of hardcoded values
    private static final String graphLocation = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\test.gps";
    private static final String ruleName = "creation";

    /**
     * Parse an OCL invariant and create the corresponding GROOVE rules
     * @param ocl   The OCL constraint(s)
     */
    public void parseOCL(String ocl) {
        Reader reader = new StringReader(ocl);
        Parser parser = new Parser(new Lexer(new PushbackReader(reader)));
        Start parseTree;
        try {
            parseTree = parser.parse();
            LinkedList<PConstraint> constraints = ((AOclFile) parseTree.getPOclFile()).getConstraint();
            for(PConstraint c: constraints) {
                parseInvariant(c);
            }
        } catch (ParserException | LexerException | IOException e) {
            e.printStackTrace();
        }
    }

    private SystemStore getSystemStore() throws IOException {
        SystemStore store = SystemStore.newStore(new File(graphLocation), false);
        store.reload();
        LOGGER.info("System store is reloaded");
        return store;
    }

    private void parseInvariant(PConstraint constraint) throws IOException {
        final AspectGraph newGraph = AspectGraph.emptyGraph(ruleName, GraphRole.RULE);
        getSystemStore().putGraphs(ResourceKind.RULE, Collections.singleton(newGraph), false);
        LOGGER.info("Rule graph is created");
    }
}
