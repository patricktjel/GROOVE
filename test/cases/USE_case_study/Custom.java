package cases.USE_case_study;

import cases.TranslateHelper;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Custom extends USECaseStudy {

    @Test
    public void inv_excludesAll() throws Exception {
        String ocl = "context Project inv excludesAll: self.department.employee->excludesAll(self.employee)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Project-->self, self--employee-->N0, N0--type:Employee-->N0], " +
                "∃([N0--type:Employee-->N0, N1--type:Department-->N1, N1--not:employee-->N0, self--type:Project-->self, self--department-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Project-->self, self--employee-->N0, self--@-->N4, self--department-->N1, N0--type:Employee-->N0, N0--@-->N4, N4--forall:-->N4, N1--type:Department-->N1, N1--not:employee-->N0, N1--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_excludes() throws Exception {
        String ocl = "context Project inv excludes: self.department.employee->excludes(self.employee)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Project-->self, self--employee-->N0, N0--type:Employee-->N0], " +
                "∃([N0--type:Employee-->N0, N1--type:Department-->N1, N1--not:employee-->N0, self--type:Project-->self, self--department-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Project-->self, self--employee-->N0, self--@-->N4, self--department-->N1, N0--type:Employee-->N0, N0--@-->N4, N4--forall:-->N4, N1--type:Department-->N1, N1--not:employee-->N0, N1--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_includes() throws Exception {
        String ocl = "context Project inv includes: self.department.employee->includes(self.employee)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Project-->self, self--employee-->N0, N0--type:Employee-->N0], " +
                "∃([N0--type:Employee-->N0, N1--type:Department-->N1, N1--employee-->N0, self--type:Project-->self, self--department-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Project-->self, self--employee-->N0, self--@-->N4, self--department-->N1, N0--type:Employee-->N0, N0--@-->N4, N4--forall:-->N4, N1--type:Department-->N1, N1--employee-->N0, N1--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void constantFirst() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Department inv constantFirst: 0 <= self.budget", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Department-->self], " +
                "∃([N0--type:int-->N0, N0--int:0-->N0, N1--type:int-->N1, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N1, N3--int:le-->N4, N4--bool:true-->N4, self--type:Department-->self, self--budget-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Department-->self, self--@-->N5, self--budget-->N1, N5--forall:-->N5, N0--type:int-->N0, N0--int:0-->N0, N0--@-->N6, N1--type:int-->N1, N1--@-->N6, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N1, N3--int:le-->N4, N3--@-->N6, N4--bool:true-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void BalkenendeNorm() throws Exception {
        assert false;
        String ocl = "context Employee inv balkenendNorm: Employee.allInstances().salary->max() = 50000";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void BalkenendeNormSelf() throws Exception {
        // TODO bug is because that attribute of max is immediately at self.
        assert false;
        String ocl = "context Employee inv balkenendNorm: self.salary->max() = 50000";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }
}
