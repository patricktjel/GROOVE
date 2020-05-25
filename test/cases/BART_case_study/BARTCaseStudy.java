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
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void civilSpeedSafety() throws Exception {
        assert false;
        String ocl = "context StationComputer inv civilSpeedSafety: self.trains()->forAll(t | t.v <= t.currentSeg().civilSpeed)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void closedGateSafety() throws Exception {
        assert false;
        String ocl = "context StationComputer inv closedGateSafety:self.trains()->forAll(t | t.nextClosedGate() <> null implies t.nose + self.wcsd(t) < t.nextClosedGate().segment.segEnd) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void crashSafety() throws Exception {
        assert false;
        String ocl = "context StationComputer inv crashSafety: self.trains()->forAll(t | t.nextTrain() <> null implies t.nose + self.wcsd(t) < t.nextTrain().nose - t.nextTrain().length) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
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
        GraphBuilder graphBuilder = map.get(condition);
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
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void vcm() throws Exception {
        String ocl = "context Train inv vcm: self.vcm >= 0.0 and self.vcm <= 80.0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Train-->self], " +
                "∃([N0--type:real-->N0, N2--type:real-->N2, N2--real:0.0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--real:ge-->N4, N4--bool:true-->N4, self--type:Train-->self, self--vcm-->N0, self--vcm-->N5, N5--type:real-->N5, N7--type:real-->N7, N7--real:80.0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--real:le-->N9, N9--bool:true-->N9]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Train-->self, self--@-->N10, self--vcm-->N0, self--vcm-->N5, N10--forall:-->N10, N0--type:real-->N0, N0--@-->N100, N2--type:real-->N2, N2--real:0.0-->N2, N2--@-->N100, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--real:ge-->N4, N3--@-->N100, N4--bool:true-->N4, N4--@-->N100, N5--type:real-->N5, N5--@-->N100, N7--type:real-->N7, N7--real:80.0-->N7, N7--@-->N100, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--real:le-->N9, N8--@-->N100, N9--bool:true-->N9, N9--@-->N100, N100--exists:-->N100, N100--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void acm() throws Exception {
        String ocl = "context Train inv acm: (self.acm >= 0.0 and self.acm <= 3.0) or (self.acm >= -2.0 and self.acm <= -0.45)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Train-->self], " +
                "∃([N0--type:real-->N0, N2--type:real-->N2, N2--real:0.0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--real:ge-->N4, N4--bool:true-->N4, self--type:Train-->self, self--acm-->N0, self--acm-->N5, N5--type:real-->N5, N7--type:real-->N7, N7--real:3.0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--real:le-->N9, N9--bool:true-->N9]) " +
                "∨ ∃([N10--type:real-->N10, N12--type:real-->N12, N12--real:-2.0-->N12, N13--prod:-->N13, N13--arg:0-->N10, N13--arg:1-->N12, N13--real:ge-->N14, N14--bool:true-->N14, self--type:Train-->self, self--acm-->N10, self--acm-->N15, N15--type:real-->N15, N18--prod:-->N18, N18--arg:0-->N15, N18--arg:1-->N17, N18--real:le-->N19, N17--type:real-->N17, N17--real:-0.45-->N17, N19--bool:true-->N19]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Train-->self, self--@-->N20, self--acm-->N0, self--acm-->N5, self--acm-->N10, self--acm-->N203, N20--forall:-->N20, N0--type:real-->N0, N0--@-->N21, N2--type:real-->N2, N2--real:0.0-->N2, N2--@-->N21, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--real:ge-->N4, N3--@-->N21, N4--bool:true-->N4, N4--@-->N21, N5--type:real-->N5, N5--@-->N21, N7--type:real-->N7, N7--real:3.0-->N7, N7--@-->N21, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--real:le-->N9, N8--@-->N21, N9--bool:true-->N9, N9--@-->N21, N21--exists:-->N21, N21--in-->N20, N201--bool:true-->N201, N201--@-->N209, N202--prod:-->N202, N202--arg:0-->N10, N202--arg:1-->N208, N202--real:ge-->N201, N202--@-->N209, N203--type:real-->N203, N203--@-->N209, N204--prod:-->N204, N204--arg:0-->N203, N204--arg:1-->N205, N204--real:le-->N206, N204--@-->N209, N205--type:real-->N205, N205--real:-0.45-->N205, N205--@-->N209, N206--bool:true-->N206, N206--@-->N209, N10--type:real-->N10, N10--@-->N209, N208--type:real-->N208, N208--real:-2.0-->N208, N208--@-->N209, N209--exists:-->N209, N209--in-->N20]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void inv_all() throws Exception {
        assert false;
        String ocl =
//                "context StationComputer " +
//                    "inv bounderies: self.sb.nextPlus()->includes(self.se) " +
//                    "inv civilSpeedSafety: self.trains()->forAll(t | t.v <= t.currentSeg().civilSpeed) " +
//                    "inv closedGateSafety:self.trains()->forAll(t | t.nextClosedGate() <> null implies t.nose + self.wcsd(t) < t.nextClosedGate().segment.segEnd) " +
//                    "inv crashSafety: self.trains()->forAll(t | t.nextTrain() <> null implies t.nose + self.wcsd(t) < t.nextTrain().nose - t.nextTrain().length) " +
                "context Segment " +
                    "inv fitting: self.next <> null implies self.next.segBegin = self.segEnd " +
//                    "inv correctLength: self.segEnd - self.segBegin = self.length " +
                    "inv track: self.next <> null implies self.track = self.next.track " +
                "context Train " +
//                    "inv line: self.orig.nextPlus()->includes(self.dest)" +
                    "inv vcm: self.vcm >= 0.0 and self.vcm <= 80.0" +
                    "inv acm: (self.acm >= 0.0 and self.acm <= 3.0) or (self.acm >= -2.0 and self.acm <= -0.45)" +
                "";
        TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
    }
}
