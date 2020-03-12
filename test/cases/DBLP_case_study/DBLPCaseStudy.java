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
        assert false;
        String ocl = "context Person inv nameIsKey: Person.allInstances()->isUnique(name)";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void withoutRepetitions() throws Exception {
        assert false;
        String ocl = "context EditedBook inv editedBookWithoutRepetitions: self.bookSection->isUnique(title) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void theSamePublisher() throws Exception {
        assert false;
        String ocl ="context Book " +
                        "inv theSamePublisher: " +
                            "if self.oclIsTypeOf(BookSeriesIssue) " +
                                "then self.publisher = self.oclAsType(BookSeriesIssue).bookSeries.publisher " +
                                "else true " +
                            "endif";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void correctPagination() throws Exception {
        assert false;
        String ocl = "context EditedBook inv correctPagination: self.bookChapter->forAll(c1, c2 | c1 <> c2 implies c1.iniPage > c2.endPage or c2.iniPage > c1.endPage) ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void compatibleYear() throws Exception {
        String ocl = "context EditedBook inv compatibleYear: self.conferenceEdition->notEmpty() implies self.publicationYear >= self.conferenceEdition.year ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION, true);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
    }

    @Test
    public void conferenceIsPublished() throws Exception {
        assert false;
        String ocl = "context ConferenceEdition inv conferenceIsPublished: " +
                        "self.editedBook->notEmpty() or " +
                        "self.bookSeriesIssue->notEmpty() or " +
                        "self.journalIssue->notEmpty() ";
        Map<LaxCondition, GraphBuilder> map = TranslateHelper.translateOCLToGraph(ocl, GRAPH_LOCATION);
        LaxCondition condition = (LaxCondition) map.keySet().toArray()[0];
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
