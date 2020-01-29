package groove;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;

import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

public class OCLParser {

    static public void main(String[] args) throws Exception {
        Reader reader = new StringReader("context Transition inv: self.preArc -> notEmpty() " +
                                            "context Place inv: self.token >= 0");
        Parser parser = new Parser(new Lexer(new PushbackReader(reader)));
        Start parsed = parser.parse();
        System.out.println(parsed);
    }
}
