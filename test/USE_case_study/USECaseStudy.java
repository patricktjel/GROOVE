package USE_case_study;

import de.tuberlin.cs.cis.ocl.parser.lexer.Lexer;
import de.tuberlin.cs.cis.ocl.parser.node.Start;
import de.tuberlin.cs.cis.ocl.parser.parser.Parser;
import groove.graph.plain.PlainGraph;
import groove.ocl.GrammarStorage;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import groove.ocl.parser.TranslateOCLToLax;
import groove.util.Log;
import org.junit.jupiter.api.Test;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.logging.Logger;

public class USECaseStudy {

    private final static Logger LOGGER = Log.getLogger(USECaseStudy.class.getName());
    private static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\USE_case_study.gps";

    @Test
    public void inv_i1a() throws Exception {
        translateOCLToGraph("context Department inv i1a: self.budget >= 0");
    }

    @Test
    public void inv_i2() throws Exception {
        translateOCLToGraph("context Department inv i2: self.employee->size() >= self.project->size()");
    }

    @Test
    public void inv_i1b() throws Exception {
        translateOCLToGraph("context Employee inv i1b: self.salary >= 0");
    }

    @Test
    public void inv_i3() throws Exception {
        translateOCLToGraph("context Employee inv i3: Employee.allInstances->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)");
    }

    @Test
    public void inv_i1c() throws Exception {
        translateOCLToGraph("context Project inv i1c: self.budget >= 0");
    }

    @Test
    public void inv_i4() throws Exception {
        translateOCLToGraph("context Project inv i4: self.budget <= self.department.budget");
    }

    @Test
    public void inv_i5() throws Exception {
        translateOCLToGraph("context Project inv i5: self.department.employee->includesAll(self.employee)");
    }

    @Test
    public void inv_all() throws Exception {
        String ocl =
                "context Department "
                    + "inv i1a: self.budget >= 0"
                    + "inv i2: self.employee->size() >= self.project->size()"
                + "context Employee "
                    + "inv i1b: self.salary >= 0"
                    + "inv i3: Employee.allInstances->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)"
                + "context Project "
                    + "inv i1c: self.budget >= 0"
                    + "inv i4: self.budget <= self.department.budget"
                    + "inv i5: self.department.employee->includesAll(self.employee)"
                ;
        translateOCLToGraph(ocl);
    }

    public void translateOCLToGraph(String ocl) throws Exception {
        GrammarStorage grammarStorage = new GrammarStorage(GRAPH_LOCATION);

        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(ocl))));
        Start parseTree = parser.parse();
        TranslateOCLToLax translateOCLToLax = new TranslateOCLToLax(grammarStorage.getTypeGraphs());

        LOGGER.info("parsing:         " + ocl);
        parseTree.apply(translateOCLToLax);

        LaxCondition condition = translateOCLToLax.getResult();
        LOGGER.info("Before simplify: " + condition.toString());
        condition.simplify();
        LOGGER.info("After simplify:  " + condition.toString());

        PlainGraph graph = GraphBuilder.laxToGraph(condition);
        grammarStorage.saveGraph(graph);
    }
}
