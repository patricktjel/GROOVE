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
    private static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\USE_case_study.gps";

    @Test
    public void inv_i1a() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Department inv i1a: self.budget >= 0", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Department-->self], " +
                "∃([self--type:Department-->self, self--budget-->N1, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
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

        String expected = "∀([self--type:Employee-->self], " +
                "∃([self--type:Employee-->self, self--salary-->N1, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void inv_i3() throws Exception {
        assert false;
        TranslateHelper.translateOCLToGraph("context Employee inv i3: Employee.allInstances->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)", GRAPH_LOCATION);
    }

    @Test
    public void inv_i1c() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Project inv i1c: self.budget >= 0", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Project-->self], " +
                "∃([self--type:Project-->self, self--budget-->N1, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void inv_i4() throws Exception {
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph("context Project inv i4: self.budget <= self.department.budget", GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Project-->self], " +
                            "∃([self--type:Project-->self, self--budget-->N1, self--department-->N2, N1--type:int-->N1, N2--type:Department-->N2, N2--budget-->N3, N3--type:int-->N3, N4--prod:-->N4, N4--arg:0-->N1, N4--arg:1-->N3, N4--int:le-->N5, N5--bool:true-->N5]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void inv_i5() throws Exception {
        TranslateHelper.translateOCLToGraph("context Project inv i5: self.department.employee->includesAll(self.employee)", GRAPH_LOCATION);
    }

    @Test
    public void inv_all() throws Exception {
        assert false;
        String ocl =
                "context Department "
                    + "inv i1a: self.budget >= 0"
//                    + "inv i2: self.employee->size() >= self.project->size()"
                + "context Employee "
                    + "inv i1b: self.salary >= 0"
//                    + "inv i3: Employee.allInstances->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)"
                + "context Project "
                    + "inv i1c: self.budget >= 0"
                    + "inv i4: self.budget <= self.department.budget"
                    + "inv i5: self.department.employee->includesAll(self.employee)"
                ;
        TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
    }
}
