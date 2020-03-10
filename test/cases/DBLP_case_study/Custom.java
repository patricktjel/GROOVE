package cases.DBLP_case_study;

import cases.TranslateHelper;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class Custom extends DBLPCaseStudy {
    @Test
    public void isEmpty1() throws Exception {
        String ocl = "context EditedBook inv isEmpty1:  self.conferenceEdition->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void isEmpty2() throws Exception {
        String ocl = "context EditedBook inv isEmpty2:  self.conferenceEdition.conferenceSeries->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }
}
