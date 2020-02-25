import groove.ocl.GraphBuilder;
import groove.ocl.lax.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;
import groove.util.Log;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.logging.Logger;

import de.tuberlin.cs.cis.ocl.parser.lexer.*;
import de.tuberlin.cs.cis.ocl.parser.node.*;
import de.tuberlin.cs.cis.ocl.parser.parser.*;
import de.tuberlin.cs.cis.ocl.parser.analysis.*;

public class parseOCL {

    private final static Logger LOGGER = Log.getLogger(parseOCL.class.getName());

    static public void main(String[] args) throws ParserException, IOException, LexerException {
        String ocl =
                "context Mother inv: self.age >= 18" +
//                "context Person inv: self.age <= self.c.age" +
//                "context a:Person inv: a.age >= 18" +
//                "context Person inv: self.age >= 18 and self.age->isEmpty()" +
//                "context Person inv: self.age->isEmpty()" +
//                "context Person inv: self.age.oclAsType(Person)" +
                "";
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();

//        parseTree.getPOclFile().apply(new TreePrinter(new PrintWriter(System.out)));
        TranslateOCLToLax translateOCLToLax = new TranslateOCLToLax();
        parseTree.apply(translateOCLToLax);

        LaxCondition condition = translateOCLToLax.getResult().simplify();
        LOGGER.info(condition.toString());

        GraphBuilder builder = new GraphBuilder("test");
        builder.laxToGraph(condition);
        builder.save();
//        testGraphBuilder();
    }

    /**
     * This constructs the graph for "context Person inv: self.age >= 18"
     */
    public static void testGraphBuilder() {
        GraphBuilder builder = new GraphBuilder("test");
        builder.addNode("forall","forall:");
        builder.addNode("person", "type:Person");

        builder.addNode("exists","exists:");
        builder.addNode("int:","int:");

        builder.addNode("prod","prod:");
        builder.addNode("bool:true", "bool:true");
        builder.addNode("int:18","int:18");

        builder.addEdge("person", "@", "forall");
        builder.addEdge("exists", "in", "forall");
        builder.addEdge("int:", "@", "exists");
        builder.addEdge("prod", "@", "exists");

        builder.addEdge("person", "age", "int:");
        builder.addEdge("prod", "arg:0", "int:");
        builder.addEdge("prod", "arg:1", "int:18");
        builder.addEdge("prod", "int:ge", "bool:true");

        builder.save();
    }

    public static class TreePrinter extends DepthFirstAdapter {
        private int depth = 0;
        private PrintWriter out;

        public TreePrinter(PrintWriter out) {
            this.out = out;
        }

        public void defaultCase(Node node) {
            indent();
            out.println(
//                    node.getClass().getName() +
//                            "\t" +
                            node.toString());
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
