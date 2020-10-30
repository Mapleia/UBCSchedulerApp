package model;

import exceptions.NoCourseFound;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.JsonReader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ScheduleMakerTest {
        public ScheduleMaker scheduleMaker;
        public TimeTable timeTable;
        public String[] timeTableTimeArr = {"Afternoon", "Evening", "Morning"};

    @BeforeEach
    public void setup() {
        timeTable = new TimeTable(2020, true, timeTableTimeArr);

        try {
            timeTable.addCourse("CPSC 210");
            timeTable.addCourse("MATH 103");
            timeTable.addCourse("BIOL 200");
            timeTable.addCourse("BIOL 140");
            timeTable.addCourse("LFS 100");

        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleMaker = new ScheduleMaker(timeTable, "user");
    }

    @Test
    public void testHasAllCourses() {
        List<Section> finalTable = scheduleMaker.getFinalTimeTable();
        Set<String> courseNames = scheduleMaker.getAllCourses().keySet();
        Set<String> sectionCodeNames = new HashSet<>();
        for (Section sec : finalTable) {
            String sectionCode = sec.getSection().substring(0, sec.getSection().length() - 4);
            sectionCodeNames.add(sectionCode);
        }

        assertTrue(courseNames.containsAll(sectionCodeNames));
    }

    @Test
    public void testErrorLog() {
        TimeTable newTimetable = new TimeTable(2020, true, timeTableTimeArr);

        try {
            newTimetable.addCourse("ACAM 250");
            newTimetable.addCourse("BIOL 155");
            newTimetable.addCourse("BIOL 201");
            newTimetable.addCourse("BIOL 203");
            newTimetable.addCourse("BIOL 204");
            newTimetable.addCourse("BIOL 205");
            newTimetable.addCourse("BIOL 210");
            newTimetable.addCourse("BIOL 260");
            newTimetable.addCourse("BIOL 234");
            newTimetable.addCourse("BIOL 301");
            newTimetable.addCourse("BIOL 306");
            newTimetable.addCourse("BIOL 310");
            newTimetable.addCourse("BIOL 327");
            newTimetable.addCourse("BIOL 335");

            ScheduleMaker scheduleMaker2 = new ScheduleMaker(newTimetable, "user");

            HashMap<String, String> errorMap = scheduleMaker2.getErrorLog();

            Set<String> allErrorCourses = errorMap.keySet();

            assertTrue(scheduleMaker2.getAllCourses().keySet().containsAll(allErrorCourses));
            assertTrue(scheduleMaker2.getErrorLog().size()> 0);
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testWaitingList() {
        try {
            timeTable.addCourse("CHEM 233");
            JSONObject obj = JsonReader.findCourseFile("CHEM", "233", timeTable);
            Section chemSection = Section.createSection(obj.getJSONObject("sections").getJSONObject("1W1"), timeTable);
            assertFalse(scheduleMaker.getFinalTimeTable()
                    .contains(chemSection));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUserName() {
        scheduleMaker.addUserName("user3");
        assertEquals("user3", scheduleMaker.getUserName());
    }

    @Test
    public void testTimeTable() {
        assertTrue(scheduleMaker.getTimeTable().getCourseList().values()
                .containsAll(timeTable.getCourseList().values()));
    }

    @Test
    public void testOtherPreferences() {
        timeTableTimeArr = new String[]{"Evening", "Morning", "Afternoon"};
        timeTable = new TimeTable(2020, true, timeTableTimeArr);

        try {
            timeTable.addCourse("CPSC 210");
            timeTable.addCourse("MATH 103");
            timeTable.addCourse("BIOL 200");
            timeTable.addCourse("BIOL 140");
            timeTable.addCourse("ACAM 250");

        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleMaker = new ScheduleMaker(timeTable, "user");
        try {
            Section sec = timeTable.getCourse("ACAM 250").get("ACAM 250 001");
            assertTrue(scheduleMaker.getFinalTimeTable().contains(sec));
        } catch (Exception e) {
            fail();
            e.printStackTrace();
        }
    }

    @Test
    public void testAddLotOfCrucialBlanks() {
        try {
            TimeTable timeTableSM = new TimeTable(2020, true, timeTableTimeArr);

            timeTableSM.addCourse("LFS 100");
            timeTableSM.addCourse("LFS 150");
            timeTableSM.addCourse("LFS 250");
            timeTableSM.addCourse("LFS 252");
            timeTableSM.addCourse("LFS 302A");
            ScheduleMaker sm = new ScheduleMaker(timeTableSM, "userSM");

            List<Section> table = sm.getFinalTimeTable();
            assertTrue(table.contains(timeTableSM.getCourse("LFS 100").get("LFS 100 XMT")));
        } catch (Exception e) {
            fail();
        }

    }

    @Test
    public void testAddLot() {
        try {

            timeTable.addCourse("LFS 100");
            timeTable.addCourse("LFS 150");
            timeTable.addCourse("LFS 250");
            timeTable.addCourse("LFS 252");
            timeTable.addCourse("LFS 302A");
            timeTable.addCourse("BIOL 155");
            ScheduleMaker sm = new ScheduleMaker(timeTable, "userSM");

            assertTrue(sm.noTypeDupeList.contains("CPSC 210 Web-Oriented Course"));
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void noCourses() {
        TimeTable tt = new TimeTable(2020, true, timeTableTimeArr);
        ScheduleMaker sm = new ScheduleMaker(tt, "user");
        assertTrue(sm.getFinalTimeTable().isEmpty());
    }
}
