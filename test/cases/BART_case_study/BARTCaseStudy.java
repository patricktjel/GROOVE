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
    public void bounderies() throws Exception {
        assert false;
        String ocl = "context StationComputer inv bounderies: self.sb.nextPlus()->includes(self.se)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void civilSpeedSafety() throws Exception {
        assert false;
        String ocl = "context StationComputer inv civilSpeedSafety: self.trains()->forAll(t | t.v <= t.currentSeg().civilSpeed)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void closedGateSafety() throws Exception {
        assert false;
        String ocl = "context StationComputer inv closedGateSafety:self.trains()->forAll(t | t.nextClosedGate() <> null implies t.nose + self.wcsd(t) < t.nextClosedGate().segment.segEnd) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void crashSafety() throws Exception {
        assert false;
        String ocl = "context StationComputer inv crashSafety: self.trains()->forAll(t | t.nextTrain() <> null implies t.nose + self.wcsd(t) < t.nextTrain().nose - t.nextTrain().length) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void fitting() throws Exception {
        String ocl = "context Segment inv fitting: self.next <> null implies self.next.segBegin = self.segEnd";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([N0--type:Segment-->N0, self--type:Segment-->self, self--next-->N0], " +
                "∃([N2--type:Segment-->N2, N2--segBegin-->N3, N3--type:int-->N3, self--type:Segment-->self, self--segEnd-->N5, self--next-->N2, N5--type:int-->N5, N6--prod:-->N6, N6--arg:0-->N3, N6--arg:1-->N5, N6--int:eq-->N7, N7--bool:true-->N7]))";
        assertEquals(expected, map.get(condition).conToString(condition));
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
        String ocl = "context Segment inv track: self.next <> null implies self.track = self.next.track";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([N0--type:Segment-->N0, self--type:Segment-->self, self--next-->N0], " +
                "∃([N2--type:Track-->N2, N2--=-->N3, N3--type:Track-->N3, N5--type:Segment-->N5, N5--track-->N3, self--type:Segment-->self, self--track-->N2, self--next-->N5]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void line() throws Exception {
        assert false;
        String ocl = "context Train inv line: self.orig.nextPlus()->includes(self.dest)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void inv_all() throws Exception {
        assert false;
        String ocl =
                "context StationComputer " +
                    "inv bounderies: self.sb.nextPlus()->includes(self.se) " +
                    "inv civilSpeedSafety: self.trains()->forAll(t | t.v <= t.currentSeg().civilSpeed) " +
                    "inv closedGateSafety:self.trains()->forAll(t | t.nextClosedGate() <> null implies t.nose + self.wcsd(t) < t.nextClosedGate().segment.segEnd) " +
                    "inv crashSafety: self.trains()->forAll(t | t.nextTrain() <> null implies t.nose + self.wcsd(t) < t.nextTrain().nose - t.nextTrain().length) " +
                "context Segment " +
                    "inv fitting: self.next <> null implies self.next.segBegin = self.segEnd " +
                    "inv correctLength: self.segEnd - self.segBegin = self.length " +
                    "inv track: self.next <> null implies self.track = self.next.track " +
                "context Train " +
                    "inv line: self.orig.nextPlus()->includes(self.dest)";
        TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
    }
}
