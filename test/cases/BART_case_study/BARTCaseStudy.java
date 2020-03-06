package cases.BART_case_study;

import cases.TranslateHelper;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BARTCaseStudy {
    private static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\BART_case_study.gps";

    @Test
    public void fitting() throws Exception {
        assert false;
        //TODO: isDefined should be exists?
        String ocl = "context Segment inv fitting: self.next.isDefined implies self.next.segBegin = self.segEnd";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void correctLength() throws Exception {
        assert false;
        String ocl = "context Segment inv correctLength: self.segEnd - self.segBegin = self.length";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void track() throws Exception {
        assert false;
        String ocl = "context Segment inv track: self.next.isDefined implies self.track = self.next.track";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

}
