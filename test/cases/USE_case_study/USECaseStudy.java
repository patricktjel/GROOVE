package cases.USE_case_study;

import cases.TranslateHelper;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Used the small USE case study which can be found in:
 * <a href="https://www.researchgate.net/publication/2637671_Validating_UML_models_and_OCL_constraints">USE case study</a>
 */
public class USECaseStudy {
    protected static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\USE_case_study.gps";

    @Test
    public void inv_i1a() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Department inv i1a: self.budget >= 0", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Department-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4, self--type:Department-->self, self--budget-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Department-->self, self--@-->N5, self--budget-->N0, N5--forall:-->N5, N0--type:int-->N0, N0--@-->N6, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N6, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:ge-->N4, N3--@-->N6, N4--bool:true-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_i2() throws Exception {
        assert false;
        TranslateHelper.translateOCLToGraph("context Department inv i2: self.employee->size() >= self.project->size()", GRAPH_LOCATION);
    }

    @Test
    public void inv_i1b() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Employee inv i1b: self.salary >= 0", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Employee-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4, self--type:Employee-->self, self--salary-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Employee-->self, self--@-->N5, self--salary-->N0, N5--forall:-->N5, N0--type:int-->N0, N0--@-->N6, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N6, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:ge-->N4, N3--@-->N6, N4--bool:true-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_i3() throws Exception {
        assert false;
        TranslateHelper.translateOCLToGraph("context Employee inv i3: Employee.allInstances()->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)", GRAPH_LOCATION);
    }

    @Test
    public void inv_i1c() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Project inv i1c: self.budget >= 0", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Project-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4, self--type:Project-->self, self--budget-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Project-->self, self--@-->N5, self--budget-->N0, N5--forall:-->N5, N0--type:int-->N0, N0--@-->N6, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N6, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:ge-->N4, N3--@-->N6, N4--bool:true-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_i4() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Project inv i4: self.budget <= self.department.budget", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Project-->self], ∃([N0--type:int-->N0, N2--type:int-->N2, N5--prod:-->N5, N5--arg:0-->N0, N5--arg:1-->N2, N5--int:le-->N6, N6--bool:true-->N6, N3--type:Department-->N3, N3--budget-->N2, self--type:Project-->self, self--budget-->N0, self--department-->N3]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Project-->self, self--@-->N7, self--budget-->N0, self--department-->N3, N7--forall:-->N7, N0--type:int-->N0, N0--@-->N8, N2--type:int-->N2, N2--@-->N8, N3--type:Department-->N3, N3--budget-->N2, N3--@-->N8, N5--prod:-->N5, N5--arg:0-->N0, N5--arg:1-->N2, N5--int:le-->N6, N5--@-->N8, N6--bool:true-->N6, N6--@-->N8, N8--exists:-->N8, N8--in-->N7]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_i5() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Project inv i5: self.department.employee->includesAll(self.employee)", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Project-->self, self--employee-->N0, N0--type:Employee-->N0], " +
                "∃([N0--type:Employee-->N0, N1--type:Department-->N1, N1--employee-->N0, self--type:Project-->self, self--department-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Project-->self, self--employee-->N0, self--@-->N4, self--department-->N1, N0--type:Employee-->N0, N0--@-->N4, N4--forall:-->N4, N1--type:Department-->N1, N1--employee-->N0, N1--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_all() throws Exception {
        String ocl =
                "context Department "
                    + "inv i1a: self.budget >= 0 "
//                    + "inv i2: self.employee->size() >= self.project->size()"
                + "context Employee "
                    + "inv i1b: self.salary >= 0 "
//                    + "inv i3: Employee.allInstances()->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)"
                 + "context Project "
                    + "inv i1c: self.budget >= 0 "
                    + "inv i4: self.budget <= self.department.budget "
                    + "inv i5: self.department.employee->includesAll(self.employee) "
                ;
        TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
    }
}
