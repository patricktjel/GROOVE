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

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void isEmpty2() throws Exception {
        String ocl = "context EditedBook inv isEmpty2: self.conferenceEdition.conferenceSeries->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceSeries-->N0, N0--not:-->N0, N1--type:ConferenceEdition-->N1, N1--conferenceSeries-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N1]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void not() throws Exception {
        String ocl = "context EditedBook inv isEmpty1: not self.conferenceEdition->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void attrGraphEQ() throws Exception {
        String ocl = "context EditedBook inv attrGraphEQ: self.year >= self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N1--type:int-->N1, self--type:EditedBook-->self, self--publicationYear-->N3, self--year-->N1, N3--type:int-->N3, N4--prod:-->N4, N4--arg:0-->N1, N4--arg:1-->N3, N4--int:ge-->N5, N5--bool:true-->N5]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void attrGraphSubset() throws Exception {
        String ocl = "context EditedBook inv attrGraphSubset: self.publicationYear >= self.bookSection.bookChapter.year";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--publicationYear-->N1, self--bookSection-->N6, N1--type:int-->N1, N2--type:BookChapter-->N2, N2--year-->N3, N3--type:int-->N3, N4--prod:-->N4, N4--arg:0-->N1, N4--arg:1-->N3, N4--int:ge-->N5, N5--bool:true-->N5, N6--type:BookSection-->N6, N6--bookChapter-->N2]))";
        assertEquals(expected, map.get(condition).conToString(condition));
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
    public void xor() throws Exception {
        assert false;
        String ocl = "context EditedBook inv xorTest: self.numPages > 0 xor self.year > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void ifthenelseNotEmpty() throws Exception {
        String ocl = "context EditedBook inv ifthenelseNotEmpty: if self.conferenceEdition->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--year-->N3, N3--type:int-->N3, N4--int:0-->N4, N5--prod:-->N5, N5--arg:0-->N3, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:true-->N6]) " +
                "∨ ∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--publicationYear-->N8, N8--type:int-->N8, N9--int:0-->N9, N10--prod:-->N10, N10--arg:0-->N8, N10--arg:1-->N9, N10--int:gt-->N11, N11--bool:true-->N11]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void ifthenelseEmpty() throws Exception {
        String ocl = "context EditedBook inv ifthenelseEmpty: if self.conferenceEdition->isEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--year-->N3, N3--type:int-->N3, N4--int:0-->N4, N5--prod:-->N5, N5--arg:0-->N3, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:true-->N6]) " +
                "∨ ∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--publicationYear-->N8, N8--type:int-->N8, N9--int:0-->N9, N10--prod:-->N10, N10--arg:0-->N8, N10--arg:1-->N9, N10--int:gt-->N11, N11--bool:true-->N11]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void ifthenelseProd() throws Exception {
        String ocl = "context EditedBook inv ifthenelseProd: if self.numPages > 0 then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--numPages-->N1, self--year-->N6, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N6--type:int-->N6, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N6, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]) " +
                "∨ ∃([self--type:EditedBook-->self, self--numPages-->N1, self--publicationYear-->N11, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:false-->N4, N14--bool:true-->N14, N13--prod:-->N13, N13--arg:0-->N11, N13--arg:1-->N12, N13--int:gt-->N14, N12--int:0-->N12, N11--type:int-->N11]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void ifthenelseAnd1() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelseand: if self.conferenceEdition->notEmpty() and self.bookSection->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void ifthenelseOr() throws Exception {
        assert false;
        String ocl = "context EditedBook inv ifthenelseand: if self.conferenceEdition->notEmpty() or self.bookSection->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
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
        String ocl = "context EditedBook inv ifthenelsetrue: if self.numPages > 0 then self.year > 0 else true endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--numPages-->N1, self--year-->N6, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N6--type:int-->N6, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N6, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]) " +
                "∨ ∃([self--type:EditedBook-->self, self--numPages-->N1, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:false-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void ifthenelsefalse() throws Exception {
        String ocl = "context EditedBook inv ifthenelsefalse: if self.numPages > 0 then self.year > 0 else false endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--numPages-->N1, self--year-->N6, N1--type:int-->N1, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N6--type:int-->N6, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N6, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void oclIsKindOf() throws Exception {
        String ocl = "context Person inv oclIsKindOf: self.authoredPublication.oclIsKindOf(AuthoredPublication)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:AuthoredPublication-->N0, self--type:Person-->self, self--authoredPublication-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void oclIsTypeOf() throws Exception {
        String ocl = "context Person inv oclIsTypeOf: self.authoredPublication.oclIsTypeOf(AuthoredPublication)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:#AuthoredPublication-->N0, self--type:Person-->self, self--authoredPublication-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void oclIsTypeOf2() throws Exception {
        assert false;
        String ocl = "context Book inv oclIsTypeOf2: self.oclIsTypeOf(BookSeriesIssue)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void oclAsType() throws Exception {
        String ocl ="context Person " +
                        "inv oclAsType: " +
                            "if self.publication.oclIsTypeOf(Book) " +
                                "then self.numPublications = self.publication.oclAsType(Book).numPages " +
                                "else true " +
                        "endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:#Book-->N0, self--type:Person-->self, self--publication-->N0, self--numPublications-->N3, self--publication-->N4, N3--type:int-->N3, N4--type:Book-->N4, N4--numPages-->N5, N5--type:int-->N5, N6--prod:-->N6, N6--arg:0-->N3, N6--arg:1-->N5, N6--int:eq-->N7, N7--bool:true-->N7]) " +
                "∨ ∃([N0--type:#Book-->N0, N0--not:-->N0, self--type:Person-->self, self--publication-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
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
                "∃([N1--type:int-->N1, self--type:EditedBook-->self, self--publicationYear-->N3, self--year-->N1, N3--type:int-->N3, N4--prod:-->N4, N4--arg:0-->N1, N4--arg:1-->N3, N4--int:eq-->N5, N5--bool:true-->N5]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void equals() throws Exception {
        String ocl = "context EditedBook inv equals: self = self.editor.editedBook";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--=-->N1, self--editor-->N2, N1--type:EditedBook-->N1, N2--type:Person-->N2, N2--editedBook-->N1]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void constantNeq() throws Exception {
        String ocl = "context EditedBook inv constantNeq: self.year <> 2020";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--year-->N1, N1--type:int-->N1, N2--int:2020-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--int:neq-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void intNeq() throws Exception {
        String ocl = "context EditedBook inv intNeq: self.year <> self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N1--type:int-->N1, self--type:EditedBook-->self, self--publicationYear-->N3, self--year-->N1, N3--type:int-->N3, N4--prod:-->N4, N4--arg:0-->N1, N4--arg:1-->N3, N4--int:neq-->N5, N5--bool:true-->N5]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void neq() throws Exception {
        String ocl = "context EditedBook inv neq: self <> self.editor.editedBook";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--not:=-->N1, self--editor-->N2, N1--type:EditedBook-->N1, N2--type:Person-->N2, N2--editedBook-->N1]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void notNull() throws Exception {
        String ocl = "context EditedBook inv notNull: self.editor <> null";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:Person-->N0, self--type:EditedBook-->self, self--editor-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void eqNull() throws Exception {
        String ocl = "context EditedBook inv eqNull: self.editor = null";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:Person-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--editor-->N0]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void exists1() throws Exception {
        String ocl = "context Person inv exists1: self.publication->exists(p:Publication | p.title = 'test')";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Person-->self], " +
                "∃([p--type:Publication-->p, p--title-->N1, self--type:Person-->self, self--publication-->p, N1--type:string-->N1, N2--string:'test'-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--string:eq-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }

    @Test
    public void exists2() throws Exception {
        assert false;
        String ocl = "context Person inv exists1: self.publication->exists(p1:Publication, p2:Publication | p1.title <> p2.title)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void forall() throws Exception {
        String ocl = "context Person inv forall: self.publication->forall(p:Publication | p.title = 'test')";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];

        String expected = "∀([self--type:Person-->self, self--publication-->p, p--type:Publication-->p], " +
                "∃([p--type:Publication-->p, p--title-->N1, N1--type:string-->N1, N2--string:'test'-->N2, N3--prod:-->N3, N3--arg:0-->N1, N3--arg:1-->N2, N3--string:eq-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, map.get(condition).conToString(condition));
    }
}
