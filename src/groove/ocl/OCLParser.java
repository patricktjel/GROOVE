package groove.ocl;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.lexer.LexerException;
import de.tuberlin.cs.cis.ocl.parser.node.AOclFile;
import de.tuberlin.cs.cis.ocl.parser.node.PConstraint;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import de.tuberlin.cs.cis.ocl.parser.parser.ParserException;
import groove.util.Log;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.logging.Logger;

public class OCLParser {
    private final static Logger LOGGER = Log.getLogger(OCLParser.class.getName());

    //todo: use variables instead of hardcoded values
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

    private void parseInvariant(PConstraint constraint) throws IOException {

    }
}
