package persistence;

import exceptions.NoCourseFound;
import exceptions.NoSectionFound;
import model.ScheduleMaker;
import model.Section;
import model.TimeTable;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileFormatTest {
    FileFormat file;
    TimeTable timeTable;
    @BeforeEach
    public void setup() {
        String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};
        timeTable = new TimeTable(2020, 0, timeTableTimeArr);

        try {
            timeTable.addCourse("CPSC 210");
            timeTable.addCourse("MATH 103");
            timeTable.addCourse("BIOL 200");
            timeTable.addCourse("BIOL 140");

        } catch (Exception e) {
            e.printStackTrace();
        }

        ScheduleMaker scheduleMaker = new ScheduleMaker(timeTable, "user");

        file = new FileFormat(scheduleMaker);
    }

    @Test
    public void testFile() {
        List<Section> sections = new ArrayList<>();

        try {
            sections.add(timeTable.getCourse("CPSC 210").get("CPSC 210 102"));
            sections.add(timeTable.getCourse("MATH 103").get("MATH 103 203"));
            sections.add(timeTable.getCourse("BIOL 200").get("BIOL 200 103"));
            sections.add(timeTable.getCourse("BIOL 140").get("BIOL 140 1B1"));

        } catch (NoSectionFound noSectionFound) {
            noSectionFound.printStackTrace();
        } catch (NoCourseFound noCourseFound) {
            noCourseFound.printStackTrace();
        }

        assertEquals("user", file.getUserName());
        assertEquals("[BIOL 140, BIOL 200, CPSC 210, MATH 103]", file.getCourses().toString());
        assertEquals("{}", file.getErrorLog().toString());
        assertTrue(file.getFinalSchedule().containsAll(sections));
        assertTrue(file.getTimeTable().getCourseList().values().containsAll(timeTable.getCourseList().values()));

    }

    @Test
    public void testCreateFile() {
        JSONObject obj = file.createObject();

        assertEquals("[\"BIOL 140\",\"BIOL 200\",\"CPSC 210\",\"MATH 103\"]", obj.get("courses").toString());
    }

}
