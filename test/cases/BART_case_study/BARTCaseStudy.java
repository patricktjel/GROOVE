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
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([N0--type:Segment-->N0, self--type:Segment-->self, self--next-->N0], " +
                "∃([N2--type:int-->N2, N5--type:int-->N5, N7--prod:-->N7, N7--arg:0-->N2, N7--arg:1-->N5, N7--int:eq-->N8, N8--bool:true-->N8, N3--type:Segment-->N3, N3--segBegin-->N2, self--type:Segment-->self, self--next-->N3, self--segEnd-->N5]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[N0--type:Segment-->N0, N0--@-->N9, self--type:Segment-->self, self--next-->N0, self--@-->N9, self--next-->N3, self--segEnd-->N5, N9--forall:-->N9, N2--type:int-->N2, N2--@-->N10, N3--type:Segment-->N3, N3--segBegin-->N2, N3--@-->N10, N5--type:int-->N5, N5--@-->N10, N7--prod:-->N7, N7--arg:0-->N2, N7--arg:1-->N5, N7--int:eq-->N8, N7--@-->N10, N8--bool:true-->N8, N8--@-->N10, N10--exists:-->N10, N10--in-->N9]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
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
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([N0--type:Segment-->N0, self--type:Segment-->self, self--next-->N0], " +
                "∃([N2--type:Track-->N2, N2--=-->N4, N4--type:Track-->N4, N5--type:Segment-->N5, N5--track-->N4, self--type:Segment-->self, self--track-->N2, self--next-->N5]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[N0--type:Segment-->N0, N0--@-->N7, self--type:Segment-->self, self--next-->N0, self--@-->N7, self--track-->N2, self--next-->N5, N7--forall:-->N7, N2--type:Track-->N2, N2--=-->N4, N2--@-->N8, N4--type:Track-->N4, N4--@-->N8, N5--type:Segment-->N5, N5--track-->N4, N5--@-->N8, N8--exists:-->N8, N8--in-->N7]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
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
