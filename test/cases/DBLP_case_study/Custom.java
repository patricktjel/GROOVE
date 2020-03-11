package cases.DBLP_case_study;

import cases.TranslateHelper;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Custom extends DBLPCaseStudy {
    @Test
    public void isEmpty1() throws Exception {
        String ocl = "context EditedBook inv isEmpty1:  self.conferenceEdition->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self, N0--type:ConferenceEdition-->N0]," +
                " ∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--not:conferenceEdition-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void isEmpty2() throws Exception {
        String ocl = "context EditedBook inv isEmpty2:  self.conferenceEdition.conferenceSeries->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self, N0--type:ConferenceSeries-->N0], " +
                "∃([N0--type:ConferenceSeries-->N0, N1--type:ConferenceEdition-->N1, N1--not:conferenceSeries-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N1]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void attrGraphEQ() throws Exception {
        String ocl = "context EditedBook inv attrGraphEQ: self.year >= self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--year-->N1, self--publicationYear-->N2, N1--type:int-->N1, N2--type:int-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:ge-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void attrGraphSubset() throws Exception {
        assert false;
        String ocl = "context EditedBook inv attrGraphSubset: self.year >= self.bookSection.bookChapter.year";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }
}
