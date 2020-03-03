package cases.USE_case_study;

import cases.TranslateHelper;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Used the small USE case study which can be found in:
 * <a href="https://www.researchgate.net/publication/2637671_Validating_UML_models_and_OCL_constraints">USE case study</a>
 */
public class USECaseStudy {
    private static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\USE_case_study.gps";

    @Test
    public void inv_i1a() throws Exception {
        LaxCondition condition = TranslateHelper.translateOCLToGraph("context Department inv i1a: self.budget >= 0", GRAPH_LOCATION);
        assertEquals(condition.toString(),
                "∀([self--type:Department-->self], " +
                        "∃([self--type:Department-->self, self--budget-->n1, n1--type:int-->n1, n2--int:0-->n2, n3--prod:-->n3, n3--arg:0-->n1, n3--arg:1-->n2, n3--int:ge-->n4, n4--bool:true-->n4]))"
        );
    }

    @Test
    public void inv_i2() throws Exception {
        TranslateHelper.translateOCLToGraph("context Department inv i2: self.employee->size() >= self.project->size()", GRAPH_LOCATION);
        assert false;
    }

    @Test
    public void inv_i1b() throws Exception {
        LaxCondition condition = TranslateHelper.translateOCLToGraph("context Employee inv i1b: self.salary >= 0", GRAPH_LOCATION);
        assertEquals(condition.toString(),
                "∀([self--type:Employee-->self], " +
                        "∃([self--type:Employee-->self, self--salary-->n1, n1--type:int-->n1, n2--int:0-->n2, n3--prod:-->n3, n3--arg:0-->n1, n3--arg:1-->n2, n3--int:ge-->n4, n4--bool:true-->n4]))"
        );
    }

    @Test
    public void inv_i3() throws Exception {
        TranslateHelper.translateOCLToGraph("context Employee inv i3: Employee.allInstances->forAll(e1, e2 | e1.project->size() > e2.project->size() implies e1.salary > e2.salary)", GRAPH_LOCATION);
        assert false;
    }

    @Test
    public void inv_i1c() throws Exception {
        LaxCondition condition = TranslateHelper.translateOCLToGraph("context Project inv i1c: self.budget >= 0", GRAPH_LOCATION);
        assertEquals(condition.toString(),
                "∀([self--type:Project-->self], " +
                        "∃([self--type:Project-->self, self--budget-->n1, n1--type:int-->n1, n2--int:0-->n2, n3--prod:-->n3, n3--arg:0-->n1, n3--arg:1-->n2, n3--int:ge-->n4, n4--bool:true-->n4]))"
        );
    }

    @Test
    public void inv_i4() throws Exception {
        TranslateHelper.translateOCLToGraph("context Project inv i4: self.budget <= self.department.budget", GRAPH_LOCATION);
        assert false;
    }

    @Test
    public void inv_i5() throws Exception {
        TranslateHelper.translateOCLToGraph("context Project inv i5: self.department.employee->includesAll(self.employee)", GRAPH_LOCATION);
        assert false;
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
        TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        assert false;
    }
}