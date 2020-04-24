package cases.DBLP_case_study;

import cases.TranslateHelper;
import groove.ocl.graphbuilder.GraphBuilder;
import groove.ocl.lax.condition.LaxCondition;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DBLPCaseStudy {
    protected static final String GRAPH_LOCATION = "C:\\Users\\patri\\Google Drive\\UT\\afstuderen\\groove\\DBLP_case_study.gps";

    @Test
    public void nameIsKey() throws Exception {
        // todo remove the additional Person node
        String ocl = "context Person inv nameIsKey: Person.allInstances()->isUnique(name)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Person-->self, N0--type:Person-->N0, N1--type:Person-->N1], " +
                "∀([N0--type:Person-->N0, N0--!=-->N1, N1--type:Person-->N1], " +
                "∃([N4--type:string-->N4, N6--type:string-->N6, N8--prod:-->N8, N8--arg:0-->N4, N8--arg:1-->N6, N8--string:neq-->N9, N9--bool:true-->N9, N0--type:Person-->N0, N0--name-->N4, N1--type:Person-->N1, N1--name-->N6])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Person-->self, self--@-->N10, N0--type:Person-->N0, N0--@-->N10, N0--!=-->N1, N0--name-->N4, N1--type:Person-->N1, N1--@-->N10, N1--name-->N6, N10--forall:-->N10, N4--type:string-->N4, N4--@-->N12, N6--type:string-->N6, N6--@-->N12, N8--prod:-->N8, N8--arg:0-->N4, N8--arg:1-->N6, N8--string:neq-->N9, N8--@-->N12, N9--bool:true-->N9, N9--@-->N12, N12--exists:-->N12, N12--in-->N10]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void withoutRepetitions() throws Exception {
        String ocl = "context EditedBook inv editedBookWithoutRepetitions: self.bookSection->isUnique(title) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self, self--bookSection-->N0, self--bookSection-->N2, N0--type:BookSection-->N0, N2--type:BookSection-->N2], " +
                "∀([N0--type:BookSection-->N0, N0--!=-->N2, N2--type:BookSection-->N2], " +
                "∃([N6--type:string-->N6, N8--type:string-->N8, N10--prod:-->N10, N10--arg:0-->N6, N10--arg:1-->N8, N10--string:neq-->N11, N11--bool:true-->N11, N0--type:BookSection-->N0, N0--title-->N6, N2--type:BookSection-->N2, N2--title-->N8])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--bookSection-->N0, self--bookSection-->N2, self--@-->N12, N0--type:BookSection-->N0, N0--@-->N12, N0--!=-->N2, N0--title-->N6, N2--type:BookSection-->N2, N2--@-->N12, N2--title-->N8, N12--forall:-->N12, N6--type:string-->N6, N6--@-->N14, N8--type:string-->N8, N8--@-->N14, N10--prod:-->N10, N10--arg:0-->N6, N10--arg:1-->N8, N10--string:neq-->N11, N10--@-->N14, N11--bool:true-->N11, N11--@-->N14, N14--exists:-->N14, N14--in-->N12]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void theSamePublisher() throws Exception {
        String ocl ="context Book inv theSamePublisher: " +
                            "if self.oclIsTypeOf(BookSeriesIssue) " +
                                "then self.publisher = self.oclAsType(BookSeriesIssue).bookSeries.publisher " +
                                "else true " +
                            "endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:Book-->self], " +
                "∃([self--type:Book-->self, self--=-->N1, self--publisher-->N2, self--=-->N6, N1--type:#BookSeriesIssue-->N1, N2--type:string-->N2, N4--type:string-->N4, N5--type:BookSeries-->N5, N5--publisher-->N4, N6--type:BookSeriesIssue-->N6, N6--bookSeries-->N5, N8--prod:-->N8, N8--arg:0-->N2, N8--arg:1-->N4, N8--string:eq-->N9, N9--bool:true-->N9]) " +
                "∨ ∃([self--type:Book-->self, self--=-->N1, N1--type:#BookSeriesIssue-->N1, N1--not:-->N1]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:Book-->self, self--@-->N11, self--=-->N1, self--publisher-->N2, self--=-->N6, self--=-->N10, N11--forall:-->N11, N1--type:#BookSeriesIssue-->N1, N1--@-->N12, N2--type:string-->N2, N2--@-->N12, N4--type:string-->N4, N4--@-->N12, N5--type:BookSeries-->N5, N5--publisher-->N4, N5--@-->N12, N6--type:BookSeriesIssue-->N6, N6--bookSeries-->N5, N6--@-->N12, N8--prod:-->N8, N8--arg:0-->N2, N8--arg:1-->N4, N8--string:eq-->N9, N8--@-->N12, N9--bool:true-->N9, N9--@-->N12, N12--exists:-->N12, N12--in-->N11, N10--type:#BookSeriesIssue-->N10, N10--not:-->N10, N10--@-->N13, N13--exists:-->N13, N13--in-->N11]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void correctPagination() throws Exception {
        String ocl = "context EditedBook inv correctPagination: self.bookChapter->forAll(c1, c2:BookChapter | c1 <> c2 implies c1.iniPage > c2.endPage or c2.iniPage > c1.endPage) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self, self--bookChapter-->c1, self--bookChapter-->c2, c1--type:BookChapter-->c1, c2--type:BookChapter-->c2], " +
                "∀([c1--type:BookChapter-->c1, c1--!=-->c2, c2--type:BookChapter-->c2], " +
                "∃([N2--type:int-->N2, N4--type:int-->N4, N6--prod:-->N6, N6--arg:0-->N2, N6--arg:1-->N4, N6--int:gt-->N7, N7--bool:true-->N7, c1--type:BookChapter-->c1, c1--iniPage-->N2, c2--type:BookChapter-->c2, c2--endPage-->N4]) " +
                "∨ ∃([N8--type:int-->N8, N10--type:int-->N10, N12--prod:-->N12, N12--arg:0-->N8, N12--arg:1-->N10, N12--int:gt-->N13, N13--bool:true-->N13, c1--type:BookChapter-->c1, c1--endPage-->N10, c2--type:BookChapter-->c2, c2--iniPage-->N8])))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--bookChapter-->c1, self--bookChapter-->c2, self--@-->N16, c1--type:BookChapter-->c1, c1--@-->N16, c1--!=-->c2, c1--iniPage-->N2, c1--endPage-->N10, c2--type:BookChapter-->c2, c2--@-->N16, c2--endPage-->N4, c2--iniPage-->N8, N16--forall:-->N16, N2--type:int-->N2, N2--@-->N18, N4--type:int-->N4, N4--@-->N18, N6--prod:-->N6, N6--arg:0-->N2, N6--arg:1-->N4, N6--int:gt-->N7, N6--@-->N18, N7--bool:true-->N7, N7--@-->N18, N18--exists:-->N18, N18--in-->N16, c10--bool:true-->c10, c10--@-->c14, N8--type:int-->N8, N8--@-->c14, N10--type:int-->N10, N10--@-->c14, c13--prod:-->c13, c13--arg:0-->N8, c13--arg:1-->N10, c13--int:gt-->c10, c13--@-->c14, c14--exists:-->c14, c14--in-->N16]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void compatibleYear() throws Exception {
        String ocl = "context EditedBook inv compatibleYear: self.conferenceEdition->notEmpty() implies self.publicationYear >= self.conferenceEdition.year ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:EditedBook-->self, self--conferenceEdition-->N0, N0--type:ConferenceEdition-->N0], " +
                "∃([N2--type:int-->N2, N4--type:int-->N4, N7--prod:-->N7, N7--arg:0-->N2, N7--arg:1-->N4, N7--int:ge-->N8, N8--bool:true-->N8, N5--type:ConferenceEdition-->N5, N5--year-->N4, self--type:EditedBook-->self, self--publicationYear-->N2, self--conferenceEdition-->N5]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:EditedBook-->self, self--conferenceEdition-->N0, self--@-->N9, self--publicationYear-->N2, self--conferenceEdition-->N5, N0--type:ConferenceEdition-->N0, N0--@-->N9, N9--forall:-->N9, N2--type:int-->N2, N2--@-->N10, N4--type:int-->N4, N4--@-->N10, N5--type:ConferenceEdition-->N5, N5--year-->N4, N5--@-->N10, N7--prod:-->N7, N7--arg:0-->N2, N7--arg:1-->N4, N7--int:ge-->N8, N7--@-->N10, N8--bool:true-->N8, N8--@-->N10, N10--exists:-->N10, N10--in-->N9]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void conferenceIsPublished() throws Exception {
        String ocl = "context ConferenceEdition inv conferenceIsPublished: " +
                        "self.editedBook->notEmpty() or " +
                        "self.bookSeriesIssue->notEmpty() or " +
                        "self.journalIssue->notEmpty() ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
        GraphBuilder graphBuilder = map.get(condition);

        String expected = "∀([self--type:ConferenceEdition-->self], " +
                "∃([N0--type:EditedBook-->N0, self--type:ConferenceEdition-->self, self--editedBook-->N0]) " +
                "∨ ∃([N2--type:BookSeriesIssue-->N2, self--type:ConferenceEdition-->self, self--bookSeriesIssue-->N2]) " +
                "∨ ∃([N4--type:JournalIssue-->N4, self--type:ConferenceEdition-->self, self--journalIssue-->N4]))";
        assertEquals(expected, graphBuilder.conToString(condition));

        String grooveExpected = "[self--type:ConferenceEdition-->self, self--@-->N6, self--editedBook-->N0, self--bookSeriesIssue-->N2, self--journalIssue-->N4, N6--forall:-->N6, N0--type:EditedBook-->N0, N0--@-->N7, N7--exists:-->N7, N7--in-->N6, N2--type:BookSeriesIssue-->N2, N2--@-->N8, N8--exists:-->N8, N8--in-->N6, N4--type:JournalIssue-->N4, N4--@-->N9, N9--exists:-->N9, N9--in-->N6]";
        assertEquals(grooveExpected, graphBuilder.graphToString(graphBuilder.laxToGraph(condition)));
    }

    @Test
    public void consecutiveVolumes() throws Exception {
        assert false;
        String ocl = "context Journal inv consecutiveVolumes:self.journalVolume->sortedBy(volume).volume = Sequence{1..self.journalVolume->size()}";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void inv_all() throws Exception {
        assert false;
        String ocl =
                "context Person " +
                    "inv nameIsKey: Person.allInstances()->isUnique(name) " +
                "context Book " +
                    "inv isbnIsKey: Book.allInstances()->isUnique(isbn) " +
                    "inv theSamePublisher: " +
                        "if self.oclIsTypeOf(BookSeriesIssue) " +
                            "then self.publisher = self.oclAsType(BookSeriesIssue).bookSeries.publisher " +
                            "else true " +
                        "endif " +
                "context EditedBook " +
                    "inv correctPagination: self.bookChapter->forAll(c1, c2 | c1 <> c2 implies c1.iniPage > c2.endPage or c2.iniPage > c1.endPage) " +
                    "inv compatibleYear: self.conferenceEdition->notEmpty() implies self.publicationYear >= self.conferenceEdition.year " +
                    "inv editedBookWithoutRepetitions: self.bookSection->isUnique(title) " +
                "context BookChapter " +
                    "inv correctPagination: self.iniPage <= self.endPage " +
                "context JournalPaper " +
                    "inv correctPagination: self.iniPage <= self.endPage " +
                "context BookSection " +
                    "inv bookSectionWithoutRepetitions: self.bookChapter->isUnique(title) " +
                "context JournalSection " +
                    "inv journalSectionWithoutRepetitions: self.journalPaper->isUnique(title) " +
                "context JournalIssue " +
                    "inv journalIssueAndTitleIdentifyJournalSection: self.journalSection->isUnique(title) " +
                    "inv correctPagination: self.journalPaper->forAll(p1, p2 | p1 <> p2 implies p1.iniPage > p2.endPage or p2.iniPage > p1.endPage) " +
                    "inv compatibleYear:self.conferenceEdition->notEmpty() implies self.year >= self.conferenceEdition.year " +
                "context BookSeriesIssue " +
                    "inv correctPagination: self.bookChapter->forAll(c1, c2 | c1 <> c2 implies c1.iniPage > c2.endPage or c2.iniPage > c1.endPage) " +
                    "inv compatibleYear:self.conferenceEdition->notEmpty() implies self.publicationYear >= self.conferenceEdition.year " +
                "context BookSeries " +
                    "inv idIsKey: BookSeries.allInstances()->isUnique(id) " +
                    "inv BookSeriesAndNumberIdentifyBookSeriesIssue: self.bookSeriesIssue->isUnique(number) " +
                "context ConferenceEdition " +
                    "inv titleIsKey:ConferenceEdition.allInstances()->isUnique(title) " +
                    "inv conferenceIsPublished: " +
                        "self.editedBook->notEmpty() or " +
                        "self.bookSeriesIssue->notEmpty() or " +
                        "self.journalIssue->notEmpty() " +
                "context ConferenceSeries " +
                    "inv nameIsKey: ConferenceSeries.allInstances()->isUnique(name) " +
                "context JournalVolume " +
                    "inv journalVolumeAndNumberIdentifyJournalIssue:self.journalIssue->isUnique(number) " +
                "context Journal " +
                    "inv titleIsKey:Journal.allInstances()->isUnique(title) " +
                    "inv journalAndVolumeIdentifyJournalVolume: self.journalVolume->isUnique(volume) " +
                    "inv consecutiveVolumes:self.journalVolume->sortedBy(volume).volume = Sequence{1..self.journalVolume->size()}";
        TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
    }
}
