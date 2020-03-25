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
        String ocl = "context EditedBook inv isEmpty1: self.conferenceEdition->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self, N0--type:ConferenceEdition-->N0]," +
                " ∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--not:conferenceEdition-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void isEmpty2() throws Exception {
        String ocl = "context EditedBook inv isEmpty2: self.conferenceEdition.conferenceSeries->isEmpty()";
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

    @Test
    public void andTest2() throws Exception {
        String ocl = "context EditedBook inv andTest2: self.numPages > 0 and self.year > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--numPages-->N1, self--year-->N6, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N6--type:int-->N6, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N6, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void andTest3() throws Exception {
        String ocl = "context EditedBook inv andTest3: self.numPages > 0 and self.year > 0 and self.publicationYear > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--numPages-->N1, self--year-->N6, self--publicationYear-->N12, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N6--type:int-->N6, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N6, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9, N14--bool:true-->N14, N10--prod:-->N10, N10--arg:0-->N12, N10--arg:1-->N11, N10--int:gt-->N14, N11--int:0-->N11, N12--type:int-->N12]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void andImpliesTest() throws Exception {
        String ocl = "context EditedBook inv andImpliesTest: self.numPages > 0 and self.year > 0 implies self.publicationYear > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self, self--numPages-->N1, self--year-->N6, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N6--type:int-->N6, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N6, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9], " +
                "∃([self--type:EditedBook-->self, self--publicationYear-->N11, N11--type:int-->N11, N12--int:0-->N12, N13--prod:-->N13, N13--arg:0-->N11, N13--arg:1-->N12, N13--int:gt-->N14, N14--bool:true-->N14]))";
        assertEquals(expected, map.get(condition).conToString(condition));
        assertEquals("andImpliesTest", condition.getGraph().getName());
    }

    @Test
    public void ifthenelseEmpty() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelseEmpty: if self.conferenceEdition->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void ifthenelseProd() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelseProd: if self.numPages > 0 then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void ifthenelseAnd1() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelseand: if self.conferenceEdition->notEmpty() and self.bookSection->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void ifthenelseAnd2() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelseEmpty: if self.conferenceEdition->notEmpty() and self.numPages > 0 then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void ifthenelsetrue() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelsetrue: if self.numPages > 0 then self.year > 0 else true endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void oclIsKindOf() throws Exception {
        assert false;
        String ocl = "context Book inv oclIsKindOf: self.oclIsKindOf(BookSeriesIssue)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void oclIsTypeOf() throws Exception {
        assert false;
        String ocl = "context Book inv oclIsTypeOf: self.oclIsTypeOf(BookSeriesIssue)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void constantEquals() throws Exception {
        String ocl = "context EditedBook inv constantEquals: self.year = 2020";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--year-->N1, N1--type:int-->N1, N2--int:2020-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:eq-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void intEquals() throws Exception {
        String ocl = "context EditedBook inv intEquals: self.year = self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--year-->N1, self--publicationYear-->N2, N1--type:int-->N1, N2--type:int-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:eq-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void equals() throws Exception {
        String ocl = "context EditedBook inv equals: self = self.editor.editedBook";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--editor-->N1, N1--type:Person-->N1, N1--editedBook-->self]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }
}
