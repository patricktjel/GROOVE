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
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N2, self--conferenceEdition-->N0, N2--forall:-->N2, N0--type:ConferenceEdition-->N0, N0--not:-->N0, N0--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void isEmpty2() throws Exception {
        String ocl = "context EditedBook inv isEmpty2: self.conferenceEdition.conferenceSeries->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceSeries-->N0, N0--not:-->N0, N1--type:ConferenceEdition-->N1, N1--conferenceSeries-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N3, self--conferenceEdition-->N1, N3--forall:-->N3, N0--type:ConferenceSeries-->N0, N0--not:-->N0, N0--@-->N4, N1--type:ConferenceEdition-->N1, N1--conferenceSeries-->N0, N1--@-->N4, N4--exists:-->N4, N4--in-->N3]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclAsSet() throws Exception {
        String ocl = "context EditedBook inv oclAsSet: self.conferenceEdition.oclAsSet()->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N2, self--conferenceEdition-->N0, N2--forall:-->N2, N0--type:ConferenceEdition-->N0, N0--not:-->N0, N0--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void not() throws Exception {
        String ocl = "context EditedBook inv notTest: not self.conferenceEdition->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N2, self--conferenceEdition-->N0, N2--forall:-->N2, N0--type:ConferenceEdition-->N0, N0--not:-->N0, N0--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void attrGraphEQ() throws Exception {
        String ocl = "context EditedBook inv attrGraphEQ: self.year >= self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:ge-->N5, N5--bool:true-->N5, self--type:EditedBook-->self, self--year-->N0, self--publicationYear-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N6, self--year-->N0, self--publicationYear-->N2, N6--forall:-->N6, N0--type:int-->N0, N0--@-->N7, N2--type:int-->N2, N2--@-->N7, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:ge-->N5, N4--@-->N7, N5--bool:true-->N5, N5--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void attrGraphSubset() throws Exception {
        String ocl = "context EditedBook inv attrGraphSubset: self.publicationYear >= self.bookSection.bookChapter.year";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N6--prod:-->N6, N6--arg:0-->N0, N6--arg:1-->N2, N6--int:ge-->N7, N7--bool:true-->N7, N3--type:BookChapter-->N3, N3--year-->N2, N4--type:BookSection-->N4, N4--bookChapter-->N3, self--type:EditedBook-->self, self--publicationYear-->N0, self--bookSection-->N4]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N8, self--publicationYear-->N0, self--bookSection-->N4, N8--forall:-->N8, N0--type:int-->N0, N0--@-->N9, N2--type:int-->N2, N2--@-->N9, N3--type:BookChapter-->N3, N3--year-->N2, N3--@-->N9, N4--type:BookSection-->N4, N4--bookChapter-->N3, N4--@-->N9, N6--prod:-->N6, N6--arg:0-->N0, N6--arg:1-->N2, N6--int:ge-->N7, N6--@-->N9, N7--bool:true-->N7, N7--@-->N9, N9--exists:-->N9, N9--in-->N8]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void andTest2() throws Exception {
        String ocl = "context EditedBook inv andTest2: self.numPages > 0 and self.year > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N10, self--numPages-->N0, self--year-->N5, N10--forall:-->N10, N0--type:int-->N0, N0--@-->N100, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N100, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N100, N4--bool:true-->N4, N4--@-->N100, N5--type:int-->N5, N5--@-->N100, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N100, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N100, N9--bool:true-->N9, N9--@-->N100, N100--exists:-->N100, N100--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void andTest3() throws Exception {
        String ocl = "context EditedBook inv andTest3: self.numPages > 0 and self.year > 0 and self.publicationYear > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, self--publicationYear-->N21, N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9, N14--bool:true-->N14, N20--prod:-->N20, N20--arg:0-->N21, N20--arg:1-->N22, N20--int:gt-->N14, N21--type:int-->N21, N22--type:int-->N22, N22--int:0-->N22]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N15, self--numPages-->N0, self--year-->N5, self--publicationYear-->N10, N15--forall:-->N15, N0--type:int-->N0, N0--@-->N16, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N16, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N16, N4--bool:true-->N4, N4--@-->N16, N5--type:int-->N5, N5--@-->N16, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N16, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N16, N9--bool:true-->N9, N9--@-->N16, N10--type:int-->N10, N10--@-->N16, N12--type:int-->N12, N12--int:0-->N12, N12--@-->N16, N14--bool:true-->N14, N14--@-->N16, N13--prod:-->N13, N13--arg:0-->N10, N13--arg:1-->N12, N13--int:gt-->N14, N13--@-->N16, N16--exists:-->N16, N16--in-->N15]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void andImpliesTest() throws Exception {
        String ocl = "context EditedBook inv andImpliesTest: self.numPages > 0 and self.year > 0 implies self.publicationYear > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9], " +
                "∃([N10--type:int-->N10, N12--type:int-->N12, N12--int:0-->N12, N13--prod:-->N13, N13--arg:0-->N10, N13--arg:1-->N12, N13--int:gt-->N14, N14--bool:true-->N14, self--type:EditedBook-->self, self--publicationYear-->N10]))";
        assertEquals(expected, graphBuilder.conToString(condition));
        assertEquals("andImpliesTest", condition.getGraph().getName());

        String grooveExpected = "[self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, self--@-->N15, self--publicationYear-->N02, N0--type:int-->N0, N0--@-->N15, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N15, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N15, N4--bool:true-->N4, N4--@-->N15, N5--type:int-->N5, N5--@-->N15, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N15, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N15, N9--bool:true-->N9, N9--@-->N15, N15--forall:-->N15, N00--bool:true-->N00, N00--@-->N04, N01--prod:-->N01, N01--arg:0-->N02, N01--arg:1-->N03, N01--int:gt-->N00, N01--@-->N04, N02--type:int-->N02, N02--@-->N04, N03--type:int-->N03, N03--int:0-->N03, N03--@-->N04, N04--exists:-->N04, N04--in-->N15]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void xor() throws Exception {
        String ocl = "context EditedBook inv xorTest: self.numPages > 0 xor self.year > 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                    "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--numPages-->N0]) " +
                    "∨ ∃([N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9, self--type:EditedBook-->self, self--year-->N5]) " +
                "∧ ∀([], " +
                    "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:false-->N4, self--type:EditedBook-->self, self--numPages-->N0]) " +
                    "∨ ∃([N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:false-->N9, self--type:EditedBook-->self, self--year-->N5])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N18, self--numPages-->N0, self--year-->N5, self--numPages-->N10, self--year-->N14, N18--forall:-->N18, N19--exists:-->N19, N19--in-->N18, N20--forall:-->N20, N20--in-->N19, N0--type:int-->N0, N0--@-->N21, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N21, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N21, N4--bool:true-->N4, N4--@-->N21, N21--exists:-->N21, N21--in-->N20, N5--type:int-->N5, N5--@-->N22, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N22, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N22, N9--bool:true-->N9, N9--@-->N22, N22--exists:-->N22, N22--in-->N20, N23--forall:-->N23, N23--in-->N19, N13--bool:false-->N13, N13--@-->N24, N10--type:int-->N10, N10--@-->N24, N12--prod:-->N12, N12--arg:0-->N10, N12--arg:1-->N11, N12--int:gt-->N13, N12--@-->N24, N11--type:int-->N11, N11--int:0-->N11, N11--@-->N24, N24--exists:-->N24, N24--in-->N23, N14--type:int-->N14, N14--@-->N25, N16--prod:-->N16, N16--arg:0-->N14, N16--arg:1-->N15, N16--int:gt-->N17, N16--@-->N25, N15--type:int-->N15, N15--int:0-->N15, N15--@-->N25, N17--bool:false-->N17, N17--@-->N25, N25--exists:-->N25, N25--in-->N23]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelseNotEmpty() throws Exception {
        String ocl = "context EditedBook inv ifthenelseNotEmpty: if self.conferenceEdition->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--year-->N2, N2--type:int-->N2, N4--type:int-->N4, N4--int:0-->N4, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:true-->N6]) " +
                "∨ ∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--publicationYear-->N7, N7--type:int-->N7, N9--type:int-->N9, N9--int:0-->N9, N10--prod:-->N10, N10--arg:0-->N7, N10--arg:1-->N9, N10--int:gt-->N11, N11--bool:true-->N11]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N13, self--conferenceEdition-->N0, self--year-->N2, self--conferenceEdition-->N12, self--publicationYear-->N7, N13--forall:-->N13, N0--type:ConferenceEdition-->N0, N0--@-->N14, N2--type:int-->N2, N2--@-->N14, N4--type:int-->N4, N4--int:0-->N4, N4--@-->N14, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N5--@-->N14, N6--bool:true-->N6, N6--@-->N14, N14--exists:-->N14, N14--in-->N13, N7--type:int-->N7, N7--@-->N133, N9--type:int-->N9, N9--int:0-->N9, N9--@-->N133, N10--prod:-->N10, N10--arg:0-->N7, N10--arg:1-->N9, N10--int:gt-->N11, N10--@-->N133, N12--type:ConferenceEdition-->N12, N12--not:-->N12, N12--@-->N133, N11--bool:true-->N11, N11--@-->N133, N133--exists:-->N133, N133--in-->N13]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelseEmpty() throws Exception {
        String ocl = "context EditedBook inv ifthenelseEmpty: if self.conferenceEdition->isEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--year-->N2, N2--type:int-->N2, N4--type:int-->N4, N4--int:0-->N4, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:true-->N6]) " +
                "∨ ∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--publicationYear-->N7, N7--type:int-->N7, N9--type:int-->N9, N9--int:0-->N9, N10--prod:-->N10, N10--arg:0-->N7, N10--arg:1-->N9, N10--int:gt-->N11, N11--bool:true-->N11]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N13, self--conferenceEdition-->N0, self--year-->N2, self--conferenceEdition-->N12, self--publicationYear-->N7, N13--forall:-->N13, N0--type:ConferenceEdition-->N0, N0--not:-->N0, N0--@-->N14, N2--type:int-->N2, N2--@-->N14, N4--type:int-->N4, N4--int:0-->N4, N4--@-->N14, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N5--@-->N14, N6--bool:true-->N6, N6--@-->N14, N14--exists:-->N14, N14--in-->N13, N7--type:int-->N7, N7--@-->N133, N9--type:int-->N9, N9--int:0-->N9, N9--@-->N133, N10--prod:-->N10, N10--arg:0-->N7, N10--arg:1-->N9, N10--int:gt-->N11, N10--@-->N133, N12--type:ConferenceEdition-->N12, N12--@-->N133, N11--bool:true-->N11, N11--@-->N133, N133--exists:-->N133, N133--in-->N13]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelseProd() throws Exception {
        String ocl = "context EditedBook inv ifthenelseProd: if self.numPages > 0 then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]) " +
                "∨ ∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:false-->N4, self--type:EditedBook-->self, self--numPages-->N0, self--publicationYear-->N10, N14--bool:true-->N14, N13--prod:-->N13, N13--arg:0-->N10, N13--arg:1-->N12, N13--int:gt-->N14, N10--type:int-->N10, N12--type:int-->N12, N12--int:0-->N12]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N19, self--numPages-->N0, self--year-->N5, self--numPages-->N15, self--publicationYear-->N10, N19--forall:-->N19, N0--type:int-->N0, N0--@-->N20, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N20, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N20, N4--bool:true-->N4, N4--@-->N20, N5--type:int-->N5, N5--@-->N20, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N20, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N20, N9--bool:true-->N9, N9--@-->N20, N20--exists:-->N20, N20--in-->N19, N14--bool:true-->N14, N14--@-->N21, N13--prod:-->N13, N13--arg:0-->N10, N13--arg:1-->N12, N13--int:gt-->N14, N13--@-->N21, N16--type:int-->N16, N16--int:0-->N16, N16--@-->N21, N15--type:int-->N15, N15--@-->N21, N18--bool:false-->N18, N18--@-->N21, N17--prod:-->N17, N17--arg:0-->N15, N17--arg:1-->N16, N17--int:gt-->N18, N17--@-->N21, N10--type:int-->N10, N10--@-->N21, N12--type:int-->N12, N12--int:0-->N12, N12--@-->N21, N21--exists:-->N21, N21--in-->N19]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelseAnd1() throws Exception {
        String ocl = "context EditedBook inv ifthenelseand: if self.conferenceEdition->notEmpty() and self.bookSection->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--bookSection-->N2, self--year-->N4, N2--type:BookSection-->N2, N4--type:int-->N4, N6--type:int-->N6, N6--int:0-->N6, N7--prod:-->N7, N7--arg:0-->N4, N7--arg:1-->N6, N7--int:gt-->N8, N8--bool:true-->N8]) " +
                    "∨ ∀([], " +
                        "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]) " +
                        "∨ ∃([N2--type:BookSection-->N2, N2--not:-->N2, self--type:EditedBook-->self, self--bookSection-->N2])) " +
                "∧ ∃([N9--type:int-->N9, N11--type:int-->N11, N11--int:0-->N11, N12--prod:-->N12, N12--arg:0-->N9, N12--arg:1-->N11, N12--int:gt-->N13, N13--bool:true-->N13, self--type:EditedBook-->self, self--publicationYear-->N9]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N16, self--conferenceEdition-->N0, self--bookSection-->N2, self--year-->N4, self--conferenceEdition-->N14, self--bookSection-->N163, self--publicationYear-->N9, N16--forall:-->N16, N0--type:ConferenceEdition-->N0, N0--@-->N17, N2--type:BookSection-->N2, N2--@-->N17, N4--type:int-->N4, N4--@-->N17, N6--type:int-->N6, N6--int:0-->N6, N6--@-->N17, N7--prod:-->N7, N7--arg:0-->N4, N7--arg:1-->N6, N7--int:gt-->N8, N7--@-->N17, N8--bool:true-->N8, N8--@-->N17, N17--exists:-->N17, N17--in-->N16, N18--exists:-->N18, N18--in-->N16, N160--forall:-->N160, N160--in-->N18, N14--type:ConferenceEdition-->N14, N14--not:-->N14, N14--@-->N20, N20--exists:-->N20, N20--in-->N160, N163--type:BookSection-->N163, N163--not:-->N163, N163--@-->N21, N21--exists:-->N21, N21--in-->N160, N13--bool:true-->N13, N13--@-->N22, N9--type:int-->N9, N9--@-->N22, N12--prod:-->N12, N12--arg:0-->N9, N12--arg:1-->N11, N12--int:gt-->N13, N12--@-->N22, N11--type:int-->N11, N11--int:0-->N11, N11--@-->N22, N22--exists:-->N22, N22--in-->N18]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelseOr() throws Exception {
        String ocl = "context EditedBook inv ifthenelseor: if self.conferenceEdition->notEmpty() or self.bookSection->notEmpty() then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]) " +
                "∨ ∃([N2--type:BookSection-->N2, self--type:EditedBook-->self, self--bookSection-->N2]) " +
                "∧ ∃([N4--type:int-->N4, N6--type:int-->N6, N6--int:0-->N6, N7--prod:-->N7, N7--arg:0-->N4, N7--arg:1-->N6, N7--int:gt-->N8, N8--bool:true-->N8, self--type:EditedBook-->self, self--year-->N4]) " +
                "∨ ∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--bookSection-->N2, self--publicationYear-->N9, N2--type:BookSection-->N2, N2--not:-->N2, N13--bool:true-->N13, N9--type:int-->N9, N12--prod:-->N12, N12--arg:0-->N9, N12--arg:1-->N11, N12--int:gt-->N13, N11--type:int-->N11, N11--int:0-->N11]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N16, self--conferenceEdition-->N0, self--bookSection-->N2, self--year-->N4, self--conferenceEdition-->N14, self--bookSection-->N165, self--publicationYear-->N9, N16--forall:-->N16, N17--exists:-->N17, N17--in-->N16, N18--forall:-->N18, N18--in-->N17, N0--type:ConferenceEdition-->N0, N0--@-->N19, N19--exists:-->N19, N19--in-->N18, N2--type:BookSection-->N2, N2--@-->N20, N20--exists:-->N20, N20--in-->N18, N4--type:int-->N4, N4--@-->N21, N6--type:int-->N6, N6--int:0-->N6, N6--@-->N21, N7--prod:-->N7, N7--arg:0-->N4, N7--arg:1-->N6, N7--int:gt-->N8, N7--@-->N21, N8--bool:true-->N8, N8--@-->N21, N21--exists:-->N21, N21--in-->N17, N14--type:ConferenceEdition-->N14, N14--not:-->N14, N14--@-->N22, N13--bool:true-->N13, N13--@-->N22, N165--type:BookSection-->N165, N165--not:-->N165, N165--@-->N22, N9--type:int-->N9, N9--@-->N22, N12--prod:-->N12, N12--arg:0-->N9, N12--arg:1-->N11, N12--int:gt-->N13, N12--@-->N22, N11--type:int-->N11, N11--int:0-->N11, N11--@-->N22, N22--exists:-->N22, N22--in-->N16]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelseAnd2() throws Exception {
        String ocl = "context EditedBook inv ifthenelseEmpty: if self.conferenceEdition->notEmpty() and self.numPages > 0 then self.year > 0 else self.publicationYear > 0 endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:ConferenceEdition-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0, self--numPages-->N2, self--year-->N7, N2--type:int-->N2, N4--type:int-->N4, N4--int:0-->N4, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:true-->N6, N7--type:int-->N7, N9--type:int-->N9, N9--int:0-->N9, N10--prod:-->N10, N10--arg:0-->N7, N10--arg:1-->N9, N10--int:gt-->N11, N11--bool:true-->N11]) " +
                    "∨ ∀([], " +
                        "∃([N0--type:ConferenceEdition-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--conferenceEdition-->N0]) " +
                        "∨ ∃([N2--type:int-->N2, N4--type:int-->N4, N4--int:0-->N4, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:false-->N6, self--type:EditedBook-->self, self--numPages-->N2])) " +
                "∧ ∃([N12--type:int-->N12, N14--type:int-->N14, N14--int:0-->N14, N15--prod:-->N15, N15--arg:0-->N12, N15--arg:1-->N14, N15--int:gt-->N16, N16--bool:true-->N16, self--type:EditedBook-->self, self--publicationYear-->N12]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N22, self--conferenceEdition-->N0, self--numPages-->N2, self--year-->N7, self--conferenceEdition-->N224, self--numPages-->N226, self--publicationYear-->N04, N22--forall:-->N22, N0--type:ConferenceEdition-->N0, N0--@-->N23, N2--type:int-->N2, N2--@-->N23, N4--type:int-->N4, N4--int:0-->N4, N4--@-->N23, N5--prod:-->N5, N5--arg:0-->N2, N5--arg:1-->N4, N5--int:gt-->N6, N5--@-->N23, N6--bool:true-->N6, N6--@-->N23, N7--type:int-->N7, N7--@-->N23, N9--type:int-->N9, N9--int:0-->N9, N9--@-->N23, N10--prod:-->N10, N10--arg:0-->N7, N10--arg:1-->N9, N10--int:gt-->N220, N10--@-->N23, N220--bool:true-->N220, N220--@-->N23, N23--exists:-->N23, N23--in-->N22, N222--exists:-->N222, N222--in-->N22, N223--forall:-->N223, N223--in-->N222, N224--type:ConferenceEdition-->N224, N224--not:-->N224, N224--@-->N225, N225--exists:-->N225, N225--in-->N223, N226--type:int-->N226, N226--@-->N27, N227--type:int-->N227, N227--int:0-->N227, N227--@-->N27, N21--bool:false-->N21, N21--@-->N27, N20--prod:-->N20, N20--arg:0-->N226, N20--arg:1-->N227, N20--int:gt-->N21, N20--@-->N27, N27--exists:-->N27, N27--in-->N223, N01--type:int-->N01, N01--int:0-->N01, N01--@-->N28, N02--bool:true-->N02, N02--@-->N28, N03--prod:-->N03, N03--arg:0-->N04, N03--arg:1-->N01, N03--int:gt-->N02, N03--@-->N28, N04--type:int-->N04, N04--@-->N28, N28--exists:-->N28, N28--in-->N222]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelsetrue() throws Exception {
        String ocl = "context EditedBook inv ifthenelsetrue: if self.numPages > 0 then self.year > 0 else true endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]) " +
                "∨ ∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:false-->N4, self--type:EditedBook-->self, self--numPages-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N14, self--numPages-->N0, self--year-->N5, self--numPages-->N10, N14--forall:-->N14, N0--type:int-->N0, N0--@-->N140, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N140, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N140, N4--bool:true-->N4, N4--@-->N140, N5--type:int-->N5, N5--@-->N140, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N140, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N140, N9--bool:true-->N9, N9--@-->N140, N140--exists:-->N140, N140--in-->N14, N141--bool:false-->N141, N141--@-->N145, N10--type:int-->N10, N10--@-->N145, N12--prod:-->N12, N12--arg:0-->N10, N12--arg:1-->N11, N12--int:gt-->N141, N12--@-->N145, N11--type:int-->N11, N11--int:0-->N11, N11--@-->N145, N145--exists:-->N145, N145--in-->N14]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void ifthenelsefalse() throws Exception {
        String ocl = "context EditedBook inv ifthenelsefalse: if self.numPages > 0 then self.year > 0 else false endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:0-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--numPages-->N0, self--year-->N5, N5--type:int-->N5, N7--type:int-->N7, N7--int:0-->N7, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N9--bool:true-->N9]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N10, self--numPages-->N0, self--year-->N5, N10--forall:-->N10, N0--type:int-->N0, N0--@-->N100, N2--type:int-->N2, N2--int:0-->N2, N2--@-->N100, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:gt-->N4, N3--@-->N100, N4--bool:true-->N4, N4--@-->N100, N5--type:int-->N5, N5--@-->N100, N7--type:int-->N7, N7--int:0-->N7, N7--@-->N100, N8--prod:-->N8, N8--arg:0-->N5, N8--arg:1-->N7, N8--int:gt-->N9, N8--@-->N100, N9--bool:true-->N9, N9--@-->N100, N100--exists:-->N100, N100--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclIsKindOf() throws Exception {
        String ocl = "context Person inv oclIsKindOf: self.publication.oclIsKindOf(Book)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--=-->N2, N2--type:Book-->N2, self--type:Person-->self, self--publication-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N3, self--publication-->N0, N3--forall:-->N3, N0--type:Publication-->N0, N0--=-->N2, N0--@-->N4, N2--type:Book-->N2, N2--@-->N4, N4--exists:-->N4, N4--in-->N3]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclIsKindOf2() throws Exception {
        String ocl = "context Publication inv oclIsKindOf2: self.oclIsKindOf(Book)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Publication-->self], " +
                "∃([self--type:Publication-->self, self--=-->N1, N1--type:Book-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Publication-->self, self--@-->N2, self--=-->N1, N2--forall:-->N2, N1--type:Book-->N1, N1--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclIsTypeOf() throws Exception {
        String ocl = "context Person inv oclIsTypeOf: self.authoredPublication.oclIsTypeOf(BookChapter)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:AuthoredPublication-->N0, N0--=-->N2, N2--type:#BookChapter-->N2, self--type:Person-->self, self--authoredPublication-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N3, self--authoredPublication-->N0, N3--forall:-->N3, N0--type:AuthoredPublication-->N0, N0--=-->N2, N0--@-->N4, N2--type:#BookChapter-->N2, N2--@-->N4, N4--exists:-->N4, N4--in-->N3]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclIsTypeOf2() throws Exception {
        String ocl = "context Book inv oclIsTypeOf2: self.oclIsTypeOf(BookSeriesIssue)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Book-->self], " +
                "∃([self--type:Book-->self, self--=-->N1, N1--type:#BookSeriesIssue-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Book-->self, self--@-->N2, self--=-->N1, N2--forall:-->N2, N1--type:#BookSeriesIssue-->N1, N1--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclAsType() throws Exception {
        String ocl ="context Person inv oclAsType: self.publication.oclAsType(Book).numPages > 1";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:int-->N0, N4--type:int-->N4, N4--int:1-->N4, N5--prod:-->N5, N5--arg:0-->N0, N5--arg:1-->N4, N5--int:gt-->N6, N6--bool:true-->N6, N1--type:Book-->N1, N1--numPages-->N0, N2--type:Publication-->N2, N2--=-->N1, self--type:Person-->self, self--publication-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N7, self--publication-->N2, N7--forall:-->N7, N0--type:int-->N0, N0--@-->N8, N1--type:Book-->N1, N1--numPages-->N0, N1--@-->N8, N2--type:Publication-->N2, N2--=-->N1, N2--@-->N8, N4--type:int-->N4, N4--int:1-->N4, N4--@-->N8, N5--prod:-->N5, N5--arg:0-->N0, N5--arg:1-->N4, N5--int:gt-->N6, N5--@-->N8, N6--bool:true-->N6, N6--@-->N8, N8--exists:-->N8, N8--in-->N7]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }
    @Test
    public void oclAsType2() throws Exception {
        String ocl ="context Publication inv oclAsType2: self.oclAsType(Book).numPages > 1";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Publication-->self], " +
                "∃([N0--type:int-->N0, N3--type:int-->N3, N3--int:1-->N3, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N3, N4--int:gt-->N5, N5--bool:true-->N5, N1--type:Book-->N1, N1--numPages-->N0, self--type:Publication-->self, self--=-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Publication-->self, self--@-->N6, self--=-->N1, N6--forall:-->N6, N0--type:int-->N0, N0--@-->N7, N1--type:Book-->N1, N1--numPages-->N0, N1--@-->N7, N3--type:int-->N3, N3--int:1-->N3, N3--@-->N7, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N3, N4--int:gt-->N5, N4--@-->N7, N5--bool:true-->N5, N5--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void oclAsType3() throws Exception {
        String ocl ="context Person inv oclAsType3: " +
                            "if self.publication.oclIsTypeOf(Book) " +
                                "then self.numPublications = self.publication.oclAsType(Book).numPages " +
                                "else true " +
                            "endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--=-->N2, N2--type:#Book-->N2, self--type:Person-->self, self--publication-->N0, self--numPublications-->N3, self--publication-->N7, N3--type:int-->N3, N5--type:int-->N5, N6--type:Book-->N6, N6--numPages-->N5, N7--type:Publication-->N7, N7--=-->N6, N9--prod:-->N9, N9--arg:0-->N3, N9--arg:1-->N5, N9--int:eq-->N10, N10--bool:true-->N10]) " +
                "∨ ∃([N0--type:Publication-->N0, N0--=-->N2, N2--type:#Book-->N2, N2--not:-->N2, self--type:Person-->self, self--publication-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N13, self--publication-->N0, self--numPublications-->N3, self--publication-->N7, self--publication-->N11, N13--forall:-->N13, N0--type:Publication-->N0, N0--=-->N2, N0--@-->N14, N2--type:#Book-->N2, N2--@-->N14, N3--type:int-->N3, N3--@-->N14, N5--type:int-->N5, N5--@-->N14, N6--type:Book-->N6, N6--numPages-->N5, N6--@-->N14, N7--type:Publication-->N7, N7--=-->N6, N7--@-->N14, N9--prod:-->N9, N9--arg:0-->N3, N9--arg:1-->N5, N9--int:eq-->N10, N9--@-->N14, N10--bool:true-->N10, N10--@-->N14, N14--exists:-->N14, N14--in-->N13, N12--type:#Book-->N12, N12--not:-->N12, N12--@-->N133, N11--type:Publication-->N11, N11--=-->N12, N11--@-->N133, N133--exists:-->N133, N133--in-->N13]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void constantEquals() throws Exception {
        String ocl = "context EditedBook inv constantEquals: self.year = 2020";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:2020-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:eq-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--year-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N5, self--year-->N0, N5--forall:-->N5, N0--type:int-->N0, N0--@-->N6, N2--type:int-->N2, N2--int:2020-->N2, N2--@-->N6, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:eq-->N4, N3--@-->N6, N4--bool:true-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void intEquals() throws Exception {
        String ocl = "context EditedBook inv intEquals: self.year = self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:eq-->N5, N5--bool:true-->N5, self--type:EditedBook-->self, self--year-->N0, self--publicationYear-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N6, self--year-->N0, self--publicationYear-->N2, N6--forall:-->N6, N0--type:int-->N0, N0--@-->N7, N2--type:int-->N2, N2--@-->N7, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:eq-->N5, N4--@-->N7, N5--bool:true-->N5, N5--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void equals() throws Exception {
        String ocl = "context EditedBook inv equals: self = self.editor.editedBook";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--=-->N1, self--editor-->N2, N1--type:EditedBook-->N1, N2--type:Person-->N2, N2--editedBook-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N4, self--=-->N1, self--editor-->N2, N4--forall:-->N4, N1--type:EditedBook-->N1, N1--@-->N5, N2--type:Person-->N2, N2--editedBook-->N1, N2--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void constantNeq() throws Exception {
        String ocl = "context EditedBook inv constantNeq: self.year <> 2020";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N2--int:2020-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:neq-->N4, N4--bool:true-->N4, self--type:EditedBook-->self, self--year-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N5, self--year-->N0, N5--forall:-->N5, N0--type:int-->N0, N0--@-->N6, N2--type:int-->N2, N2--int:2020-->N2, N2--@-->N6, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--int:neq-->N4, N3--@-->N6, N4--bool:true-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void intNeq() throws Exception {
        String ocl = "context EditedBook inv intNeq: self.year <> self.publicationYear";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:neq-->N5, N5--bool:true-->N5, self--type:EditedBook-->self, self--year-->N0, self--publicationYear-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N6, self--year-->N0, self--publicationYear-->N2, N6--forall:-->N6, N0--type:int-->N0, N0--@-->N7, N2--type:int-->N2, N2--@-->N7, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:neq-->N5, N4--@-->N7, N5--bool:true-->N5, N5--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void neq() throws Exception {
        String ocl = "context EditedBook inv neq: self <> self.editor.editedBook";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([self--type:EditedBook-->self, self--!=-->N1, self--editor-->N2, N1--type:EditedBook-->N1, N2--type:Person-->N2, N2--editedBook-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N4, self--!=-->N1, self--editor-->N2, N4--forall:-->N4, N1--type:EditedBook-->N1, N1--@-->N5, N2--type:Person-->N2, N2--editedBook-->N1, N2--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void notNull() throws Exception {
        String ocl = "context EditedBook inv notNull: self.editor <> null";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:Person-->N0, self--type:EditedBook-->self, self--editor-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N2, self--editor-->N0, N2--forall:-->N2, N0--type:Person-->N0, N0--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void eqNull() throws Exception {
        String ocl = "context EditedBook inv eqNull: self.editor = null";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:Person-->N0, N0--not:-->N0, self--type:EditedBook-->self, self--editor-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N2, self--editor-->N0, N2--forall:-->N2, N0--type:Person-->N0, N0--not:-->N0, N0--@-->N3, N3--exists:-->N3, N3--in-->N2]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void exists1() throws Exception {
        String ocl = "context Person inv exists1: self.publication->exists(p:Publication | p.title = 'test')";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([p--type:Publication-->p, p--title-->N0, self--type:Person-->self, self--publication-->p, N0--type:string-->N0, N2--type:string-->N2, N2--string:'test'-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--string:eq-->N4, N4--bool:true-->N4]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N6, self--publication-->p, N6--forall:-->N6, p--type:Publication-->p, p--title-->N0, p--@-->N7, N0--type:string-->N0, N0--@-->N7, N2--type:string-->N2, N2--string:'test'-->N2, N2--@-->N7, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--string:eq-->N4, N3--@-->N7, N4--bool:true-->N4, N4--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void exists2() throws Exception {
        String ocl = "context Person inv exists2: self.publication->exists(p1,p2:Publication | p1.title <> p2.title)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([p1--type:Publication-->p1, p1--title-->N0, p2--type:Publication-->p2, p2--title-->N2, N0--type:string-->N0, N2--type:string-->N2, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--string:neq-->N5, N5--bool:true-->N5, self--type:Person-->self, self--publication-->p1, self--publication-->p2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N8, self--publication-->p1, self--publication-->p2, N8--forall:-->N8, p1--type:Publication-->p1, p1--title-->N0, p1--@-->N9, p2--type:Publication-->p2, p2--title-->N2, p2--@-->N9, N0--type:string-->N0, N0--@-->N9, N2--type:string-->N2, N2--@-->N9, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--string:neq-->N5, N4--@-->N9, N5--bool:true-->N5, N5--@-->N9, N9--exists:-->N9, N9--in-->N8]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void forall() throws Exception {
        String ocl = "context Person inv forAll: self.publication->forAll(p:Publication | p.title = 'test')";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self, self--publication-->p, p--type:Publication-->p], " +
                "∃([N0--type:string-->N0, N2--type:string-->N2, N2--string:'test'-->N2, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--string:eq-->N4, N4--bool:true-->N4, p--type:Publication-->p, p--title-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--publication-->p, self--@-->N6, p--type:Publication-->p, p--@-->N6, p--title-->N0, N6--forall:-->N6, N0--type:string-->N0, N0--@-->N7, N2--type:string-->N2, N2--string:'test'-->N2, N2--@-->N7, N3--prod:-->N3, N3--arg:0-->N0, N3--arg:1-->N2, N3--string:eq-->N4, N3--@-->N7, N4--bool:true-->N4, N4--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void forall2() throws Exception {
        String ocl = "context Person inv forAll2: self.publication->forAll(p1,p2:Publication | p1.year = p2.year)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self, self--publication-->p1, self--publication-->p2, p1--type:Publication-->p1, p2--type:Publication-->p2], " +
                "∃([N0--type:int-->N0, N2--type:int-->N2, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:eq-->N5, N5--bool:true-->N5, p1--type:Publication-->p1, p1--year-->N0, p2--type:Publication-->p2, p2--year-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--publication-->p1, self--publication-->p2, self--@-->N8, p1--type:Publication-->p1, p1--@-->N8, p1--year-->N0, p2--type:Publication-->p2, p2--@-->N8, p2--year-->N2, N8--forall:-->N8, N0--type:int-->N0, N0--@-->N9, N2--type:int-->N2, N2--@-->N9, N4--prod:-->N4, N4--arg:0-->N0, N4--arg:1-->N2, N4--int:eq-->N5, N4--@-->N9, N5--bool:true-->N5, N5--@-->N9, N9--exists:-->N9, N9--in-->N8]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void attrIsUnique() throws Exception {
        String ocl = "context Person inv isUnique: self.publication->isUnique(year)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self, self--publication-->N0, self--publication-->N2, N0--type:Publication-->N0, N2--type:Publication-->N2], " +
                "∀([N0--type:Publication-->N0, N0--!=-->N2, N2--type:Publication-->N2], " +
                "∃([N6--type:int-->N6, N8--type:int-->N8, N10--prod:-->N10, N10--arg:0-->N6, N10--arg:1-->N8, N10--int:neq-->N11, N11--bool:true-->N11, N0--type:Publication-->N0, N0--year-->N6, N2--type:Publication-->N2, N2--year-->N8])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--publication-->N0, self--publication-->N2, self--@-->N12, N0--type:Publication-->N0, N0--@-->N12, N0--!=-->N2, N0--year-->N6, N2--type:Publication-->N2, N2--@-->N12, N2--year-->N8, N12--forall:-->N12, N6--type:int-->N6, N6--@-->N14, N8--type:int-->N8, N8--@-->N14, N10--prod:-->N10, N10--arg:0-->N6, N10--arg:1-->N8, N10--int:neq-->N11, N10--@-->N14, N11--bool:true-->N11, N11--@-->N14, N14--exists:-->N14, N14--in-->N12]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeGE2() throws Exception {
        String ocl = "context Person inv sizeGE2: self.publication->size() >= 2";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--!=-->N2, N2--type:Publication-->N2, self--type:Person-->self, self--publication-->N0, self--publication-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N4, self--publication-->N0, self--publication-->N2, N4--forall:-->N4, N0--type:Publication-->N0, N0--!=-->N2, N0--@-->N5, N2--type:Publication-->N2, N2--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeGT2() throws Exception {
        String ocl = "context Person inv sizeGT2: self.publication->size() > 2";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--!=-->N2, N0--!=-->N4, N2--type:Publication-->N2, N2--!=-->N4, N4--type:Publication-->N4, self--type:Person-->self, self--publication-->N0, self--publication-->N2, self--publication-->N4]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N6, self--publication-->N0, self--publication-->N2, self--publication-->N4, N6--forall:-->N6, N0--type:Publication-->N0, N0--!=-->N2, N0--!=-->N4, N0--@-->N7, N2--type:Publication-->N2, N2--!=-->N4, N2--@-->N7, N4--type:Publication-->N4, N4--@-->N7, N7--exists:-->N7, N7--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeEQ2() throws Exception {
        String ocl = "context Person inv sizeEQ2: self.publication->size() = 2";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--!=-->N2, N2--type:Publication-->N2, self--type:Person-->self, self--publication-->N0, self--publication-->N2, self--publication-->N4, self--publication-->N6, self--publication-->N8, N4--type:Publication-->N4, N4--not:-->N4, N4--!=-->N6, N4--!=-->N8, N6--type:Publication-->N6, N6--not:-->N6, N6--!=-->N8, N8--type:Publication-->N8, N8--not:-->N8]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N10, self--publication-->N0, self--publication-->N2, self--publication-->N4, self--publication-->N6, self--publication-->N8, N10--forall:-->N10, N0--type:Publication-->N0, N0--!=-->N2, N0--@-->N11, N2--type:Publication-->N2, N2--@-->N11, N4--type:Publication-->N4, N4--not:-->N4, N4--!=-->N6, N4--!=-->N8, N4--@-->N11, N6--type:Publication-->N6, N6--not:-->N6, N6--!=-->N8, N6--@-->N11, N8--type:Publication-->N8, N8--not:-->N8, N8--@-->N11, N11--exists:-->N11, N11--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeLE2() throws Exception {
        String ocl = "context Person inv sizeLE2: self.publication->size() <= 1";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--not:-->N0, N0--!=-->N2, N2--type:Publication-->N2, N2--not:-->N2, self--type:Person-->self, self--publication-->N0, self--publication-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N4, self--publication-->N0, self--publication-->N2, N4--forall:-->N4, N0--type:Publication-->N0, N0--not:-->N0, N0--!=-->N2, N0--@-->N5, N2--type:Publication-->N2, N2--not:-->N2, N2--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeLT2() throws Exception {
        String ocl = "context Person inv sizeLT2: self.publication->size() < 2";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--not:-->N0, N0--!=-->N2, N2--type:Publication-->N2, N2--not:-->N2, self--type:Person-->self, self--publication-->N0, self--publication-->N2]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N4, self--publication-->N0, self--publication-->N2, N4--forall:-->N4, N0--type:Publication-->N0, N0--not:-->N0, N0--!=-->N2, N0--@-->N5, N2--type:Publication-->N2, N2--not:-->N2, N2--@-->N5, N5--exists:-->N5, N5--in-->N4]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeNEQ2() throws Exception {
        String ocl = "context Person inv sizeNEQ2: self.publication->size() <> 2";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--not:-->N0, N0--!=-->N2, N2--type:Publication-->N2, N2--not:-->N2, self--type:Person-->self, self--publication-->N0, self--publication-->N2]) " +
                "∨ ∃([N4--type:Publication-->N4, N4--!=-->N6, N4--!=-->N8, N6--type:Publication-->N6, N6--!=-->N8, N8--type:Publication-->N8, self--type:Person-->self, self--publication-->N4, self--publication-->N6, self--publication-->N8]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N10, self--publication-->N0, self--publication-->N2, self--publication-->N4, self--publication-->N6, self--publication-->N8, N10--forall:-->N10, N0--type:Publication-->N0, N0--not:-->N0, N0--!=-->N2, N0--@-->N11, N2--type:Publication-->N2, N2--not:-->N2, N2--@-->N11, N11--exists:-->N11, N11--in-->N10, N4--type:Publication-->N4, N4--!=-->N6, N4--!=-->N8, N4--@-->N12, N6--type:Publication-->N6, N6--!=-->N8, N6--@-->N12, N8--type:Publication-->N8, N8--@-->N12, N12--exists:-->N12, N12--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void sizeGE5() throws Exception {
        String ocl = "context Person inv sizeGE5: self.publication->size() >= 5";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--!=-->N2, N0--!=-->N4, N0--!=-->N6, N0--!=-->N8, N2--type:Publication-->N2, N2--!=-->N4, N2--!=-->N6, N2--!=-->N8, N4--type:Publication-->N4, N4--!=-->N6, N4--!=-->N8, N6--type:Publication-->N6, N6--!=-->N8, N8--type:Publication-->N8, self--type:Person-->self, self--publication-->N0, self--publication-->N2, self--publication-->N4, self--publication-->N6, self--publication-->N8]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N10, self--publication-->N0, self--publication-->N2, self--publication-->N4, self--publication-->N6, self--publication-->N8, N10--forall:-->N10, N0--type:Publication-->N0, N0--!=-->N2, N0--!=-->N4, N0--!=-->N6, N0--!=-->N8, N0--@-->N11, N2--type:Publication-->N2, N2--!=-->N4, N2--!=-->N6, N2--!=-->N8, N2--@-->N11, N4--type:Publication-->N4, N4--!=-->N6, N4--!=-->N8, N4--@-->N11, N6--type:Publication-->N6, N6--!=-->N8, N6--@-->N11, N8--type:Publication-->N8, N8--@-->N11, N11--exists:-->N11, N11--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void min() throws Exception {
        String ocl = "context Person inv min: self.publication.year->min() = 0";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:int-->N0, N8--type:int-->N8, N8--int:0-->N8, N9--prod:-->N9, N9--arg:0-->N0, N9--arg:1-->N8, N9--int:eq-->N10, N10--bool:true-->N10, N1--type:Publication-->N1, N1--year-->N0], " +
                    "∃([N1--type:Publication-->N1, self--type:Person-->self, self--publication-->N1]) " +
                    "∧ ∀([N3--type:Publication-->N3, N3--year-->N5, N5--type:int-->N5, self--type:Person-->self, self--publication-->N3], " +
                        "∃([N0--type:int-->N0, N5--type:int-->N5, N6--prod:-->N6, N6--arg:0-->N0, N6--arg:1-->N5, N6--int:le-->N7, N7--bool:true-->N7]))))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N11, self--publication-->N1, self--publication-->N3, N11--forall:-->N11, N0--type:int-->N0, N0--@-->N12, N1--type:Publication-->N1, N1--year-->N0, N1--@-->N12, N8--type:int-->N8, N8--int:0-->N8, N8--@-->N12, N9--prod:-->N9, N9--arg:0-->N0, N9--arg:1-->N8, N9--int:eq-->N10, N9--@-->N12, N10--bool:true-->N10, N10--@-->N12, N12--exists:-->N12, N12--in-->N11, N3--type:Publication-->N3, N3--year-->N5, N3--@-->N111, N5--type:int-->N5, N5--@-->N111, N111--forall:-->N111, N111--in-->N12, N6--prod:-->N6, N6--arg:0-->N0, N6--arg:1-->N5, N6--int:le-->N7, N6--@-->N114, N7--bool:true-->N7, N7--@-->N114, N114--exists:-->N114, N114--in-->N111]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void max() throws Exception {
        String ocl = "context Person inv max: self.publication.year->max() < 5";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:int-->N0, N8--type:int-->N8, N8--int:5-->N8, N9--prod:-->N9, N9--arg:0-->N0, N9--arg:1-->N8, N9--int:lt-->N10, N10--bool:true-->N10, N1--type:Publication-->N1, N1--year-->N0], " +
                    "∃([N1--type:Publication-->N1, self--type:Person-->self, self--publication-->N1]) " +
                    "∧ ∀([N3--type:Publication-->N3, N3--year-->N5, N5--type:int-->N5, self--type:Person-->self, self--publication-->N3], " +
                        "∃([N0--type:int-->N0, N5--type:int-->N5, N6--prod:-->N6, N6--arg:0-->N0, N6--arg:1-->N5, N6--int:ge-->N7, N7--bool:true-->N7]))))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N11, self--publication-->N1, self--publication-->N3, N11--forall:-->N11, N0--type:int-->N0, N0--@-->N12, N1--type:Publication-->N1, N1--year-->N0, N1--@-->N12, N8--type:int-->N8, N8--int:5-->N8, N8--@-->N12, N9--prod:-->N9, N9--arg:0-->N0, N9--arg:1-->N8, N9--int:lt-->N10, N9--@-->N12, N10--bool:true-->N10, N10--@-->N12, N12--exists:-->N12, N12--in-->N11, N3--type:Publication-->N3, N3--year-->N5, N3--@-->N111, N5--type:int-->N5, N5--@-->N111, N111--forall:-->N111, N111--in-->N12, N6--prod:-->N6, N6--arg:0-->N0, N6--arg:1-->N5, N6--int:ge-->N7, N6--@-->N114, N7--bool:true-->N7, N7--@-->N114, N114--exists:-->N114, N114--in-->N111]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void one() throws Exception {
        assert false;
        String ocl = "context Person inv one: self.publication->one(p:Publication | p.year = 2000)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void union() throws Exception {
        String ocl = "context Person inv union: self.editedBook->union(self.authoredPublication)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0], " +
                    "∃([N0--type:Publication-->N0, N0--=-->N1, N1--type:EditedBook-->N1, self--type:Person-->self, self--editedBook-->N1]) " +
                    "∨ ∃([N0--type:Publication-->N0, N0--=-->N2, N2--type:AuthoredPublication-->N2, self--type:Person-->self, self--authoredPublication-->N2])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N5, self--editedBook-->N1, self--authoredPublication-->N2, N5--forall:-->N5, N0--type:Publication-->N0, N0--@-->N6, N0--=-->N1, N0--=-->N2, N6--exists:-->N6, N6--in-->N5, N7--forall:-->N7, N7--in-->N6, N1--type:EditedBook-->N1, N1--@-->N8, N8--exists:-->N8, N8--in-->N7, N2--type:AuthoredPublication-->N2, N2--@-->N9, N9--exists:-->N9, N9--in-->N7]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void unionNoParent() throws Exception {
        String ocl = "context EditedBook inv unionNoParent: self.bookSection->union(self.bookChapter)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self], " +
                "∃([N0--type:Object-->N0], " +
                    "∃([N0--type:Object-->N0, N0--=-->N1, N1--type:BookSection-->N1, self--type:EditedBook-->self, self--bookSection-->N1]) " +
                    "∨ ∃([N0--type:Object-->N0, N0--=-->N2, N2--type:BookChapter-->N2, self--type:EditedBook-->self, self--bookChapter-->N2])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--@-->N5, self--bookSection-->N1, self--bookChapter-->N2, N5--forall:-->N5, N0--type:Object-->N0, N0--@-->N6, N0--=-->N1, N0--=-->N2, N6--exists:-->N6, N6--in-->N5, N7--forall:-->N7, N7--in-->N6, N1--type:BookSection-->N1, N1--@-->N8, N8--exists:-->N8, N8--in-->N7, N2--type:BookChapter-->N2, N2--@-->N9, N9--exists:-->N9, N9--in-->N7]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void intersect() throws Exception {
        assert false;
        String ocl = "context Person inv intersect: self.editedBook->intersection(self.publication)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION, true);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void minus() throws Exception {
        assert false;
        String ocl = "context Person inv minus: (self.editedBook - self.publication)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void symmetricDifference() throws Exception {
        assert false;
        String ocl = "context Person inv symmetricDifference: self.editedBook->symmetricDifference(self.publication)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void including() throws Exception {
        assert false;
        String ocl = "context Person inv including: self.editedBook->including(self.publication)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void excluding() throws Exception {
        assert false;
        String ocl = "context Person inv excluding: self.editedBook->excluding(self.publication)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void allInstances() throws Exception {
        String ocl = "context Person inv allInstances: EditedBook.allInstances()->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], ∃([N0--type:EditedBook-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N1, N1--forall:-->N1, N0--type:EditedBook-->N0, N0--@-->N2, N2--exists:-->N2, N2--in-->N1]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void selectNotEmpty() throws Exception {
        String ocl = "context Person inv selectNotEmpty: self.editedBook->select(e:EditedBook | e.bookSection->notEmpty())->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N2--type:EditedBook-->N2, N2--bookSection-->N4, self--type:Person-->self, self--editedBook-->N2, N4--type:BookSection-->N4]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N5, self--editedBook-->N2, N5--forall:-->N5, N2--type:EditedBook-->N2, N2--bookSection-->N4, N2--@-->N6, N4--type:BookSection-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void selectSizeGT() throws Exception {
        String ocl = "context Person inv selectSize: self.editedBook->select(e:EditedBook | e.year > 0)->size() > 1";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N5--type:EditedBook-->N5, N5--!=-->N11, N5--year-->N7, N11--type:EditedBook-->N11, N11--year-->N13, N14--type:int-->N14, N14--int:0-->N14, N13--type:int-->N13, N16--bool:true-->N16, N15--prod:-->N15, N15--arg:0-->N13, N15--arg:1-->N14, N15--int:gt-->N16, self--type:Person-->self, self--editedBook-->N5, self--editedBook-->N11, N7--type:int-->N7, N8--type:int-->N8, N8--int:0-->N8, N9--prod:-->N9, N9--arg:0-->N7, N9--arg:1-->N8, N9--int:gt-->N10, N10--bool:true-->N10]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N17, self--editedBook-->N5, self--editedBook-->N11, N17--forall:-->N17, N14--type:int-->N14, N14--int:0-->N14, N14--@-->N18, N13--type:int-->N13, N13--@-->N18, N16--bool:true-->N16, N16--@-->N18, N15--prod:-->N15, N15--arg:0-->N13, N15--arg:1-->N14, N15--int:gt-->N16, N15--@-->N18, N5--type:EditedBook-->N5, N5--!=-->N11, N5--year-->N7, N5--@-->N18, N7--type:int-->N7, N7--@-->N18, N8--type:int-->N8, N8--int:0-->N8, N8--@-->N18, N9--prod:-->N9, N9--arg:0-->N7, N9--arg:1-->N8, N9--int:gt-->N10, N9--@-->N18, N10--bool:true-->N10, N10--@-->N18, N11--type:EditedBook-->N11, N11--year-->N13, N11--@-->N18, N18--exists:-->N18, N18--in-->N17]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void selectIsEmpty() throws Exception {
        // TODO; translation limitation??
        // negating of the EB is not possible in GROOVE
        assert false;
        String ocl = "context Person inv selectIsEmpty: self.editedBook->select(e:EditedBook | e.bookSection->notEmpty())->isEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void rejectNotEmpty() throws Exception {
        String ocl = "context Person inv rejectNotEmpty: self.editedBook->reject(e:EditedBook | e.bookSection->isEmpty())->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N2--type:EditedBook-->N2, N2--bookSection-->N4, self--type:Person-->self, self--editedBook-->N2, N4--type:BookSection-->N4]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N5, self--editedBook-->N2, N5--forall:-->N5, N2--type:EditedBook-->N2, N2--bookSection-->N4, N2--@-->N6, N4--type:BookSection-->N4, N4--@-->N6, N6--exists:-->N6, N6--in-->N5]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void rejectSizeGT() throws Exception {
        String ocl = "context Person inv rejectSize: self.editedBook->reject(e:EditedBook | e.year > 0)->size() > 1";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N5--type:EditedBook-->N5, N5--!=-->N11, N5--year-->N7, N11--type:EditedBook-->N11, N11--year-->N13, N14--type:int-->N14, N14--int:0-->N14, N13--type:int-->N13, N16--bool:false-->N16, N15--prod:-->N15, N15--arg:0-->N13, N15--arg:1-->N14, N15--int:gt-->N16, self--type:Person-->self, self--editedBook-->N5, self--editedBook-->N11, N7--type:int-->N7, N8--type:int-->N8, N8--int:0-->N8, N9--prod:-->N9, N9--arg:0-->N7, N9--arg:1-->N8, N9--int:gt-->N10, N10--bool:false-->N10]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N17, self--editedBook-->N5, self--editedBook-->N11, N17--forall:-->N17, N14--type:int-->N14, N14--int:0-->N14, N14--@-->N18, N13--type:int-->N13, N13--@-->N18, N16--bool:false-->N16, N16--@-->N18, N15--prod:-->N15, N15--arg:0-->N13, N15--arg:1-->N14, N15--int:gt-->N16, N15--@-->N18, N5--type:EditedBook-->N5, N5--!=-->N11, N5--year-->N7, N5--@-->N18, N7--type:int-->N7, N7--@-->N18, N8--type:int-->N8, N8--int:0-->N8, N8--@-->N18, N9--prod:-->N9, N9--arg:0-->N7, N9--arg:1-->N8, N9--int:gt-->N10, N9--@-->N18, N10--bool:false-->N10, N10--@-->N18, N11--type:EditedBook-->N11, N11--year-->N13, N11--@-->N18, N18--exists:-->N18, N18--in-->N17]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void selectTypeNotEmpty() throws Exception {
        String ocl = "context Person inv selectTypeNotEmpty: self.publication->selectByType(Book)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--=-->N1, N1--type:#Book-->N1, self--type:Person-->self, self--publication-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N3, self--publication-->N0, N3--forall:-->N3, N0--type:Publication-->N0, N0--=-->N1, N0--@-->N4, N1--type:#Book-->N1, N1--@-->N4, N4--exists:-->N4, N4--in-->N3]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void selectKindNotEmpty() throws Exception {
        String ocl = "context Person inv selectKindNotEmpty: self.publication->selectByKind(Book)->notEmpty()";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self], " +
                "∃([N0--type:Publication-->N0, N0--=-->N1, N1--type:Book-->N1, self--type:Person-->self, self--publication-->N0]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N3, self--publication-->N0, N3--forall:-->N3, N0--type:Publication-->N0, N0--=-->N1, N0--@-->N4, N1--type:Book-->N1, N1--@-->N4, N4--exists:-->N4, N4--in-->N3]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void setEquals() throws Exception {
        assert false;
        String ocl = "context EditedBook inv setEquals: self.bookSection.bookChapter = self.bookChapter";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION, true);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }

    @Test
    public void setEquals2() throws Exception {
        assert false;
        String ocl = "context Person inv setEquals2: self.publication = self.editedBook";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION, true);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);
    }
}
