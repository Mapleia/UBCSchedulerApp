package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        timeTable = new TimeTable(2020, 0);
        timeTable.setTimePref(timeTableTimeArr);

        try {
            timeTable.addCourse("CPSC", "210");
            timeTable.addCourse("MATH", "103");
            timeTable.addCourse("BIOL", "200");
            timeTable.addCourse("BIOL", "140");

        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleMaker = new ScheduleMaker(timeTable);
    }

    @Test
    public void testHasAllCourses() {
        scheduleMaker.makeTimeTable();
        List<Section> finalTable = scheduleMaker.getFinalTimeTable();

        Set<String> courseCodeNames = new HashSet<>();
        for (Course course : timeTable.getCourseList()) {
            courseCodeNames.add(course.getSubjectCode() + " " + course.getCourseNum());
        }

        Set<String> sectionCodeNames = new HashSet<>();
        for (Section sec : finalTable) {
            String sectionCode = sec.getSection().substring(0, sec.getSection().length() - 4);
            sectionCodeNames.add(sectionCode);
        }

        assertTrue(courseCodeNames.containsAll(sectionCodeNames));
    }

    @Test
    public void testErrorLog() {
        try {
            timeTable.addCourse("ACAM", "250");
            timeTable.addCourse("BIOL", "155");
            timeTable.addCourse("BIOL", "201");
            timeTable.addCourse("BIOL", "203");
            timeTable.addCourse("BIOL", "204");
            timeTable.addCourse("BIOL", "205");
            timeTable.addCourse("BIOL", "210");
            timeTable.addCourse("BIOL", "260");
            timeTable.addCourse("BIOL", "234");
            timeTable.addCourse("BIOL", "301");
            timeTable.addCourse("BIOL", "306");
            timeTable.addCourse("BIOL", "310");
            timeTable.addCourse("BIOL", "327");
            timeTable.addCourse("BIOL", "335");

        } catch (Exception e) {
            e.printStackTrace();
        }

        scheduleMaker.makeTimeTable();

        HashMap<String, String> errorMap = scheduleMaker.getErrorLog();

        Set<String> allErrorCourses = errorMap.keySet();
        Set<String> allCourseNames = new HashSet<>();

        for (Course c : scheduleMaker.getAllCourses()) {
            allCourseNames.add(c.getSubjectCode() + "-" + c.getCourseNum());
        }
        assertTrue(allCourseNames.containsAll(allErrorCourses));
        assertTrue(scheduleMaker.getErrorLog().size()> 0);
    }

    @Test
    public void testWaitingList() {
        try {
            timeTable.addCourse("CHEM", "233");
        } catch (Exception e) {
            fail();
        }
        scheduleMaker.makeTimeTable();

        assertFalse(scheduleMaker.getFinalTimeTable()
                .contains(new Section("CHEM 233 1W1", "Waiting List")));
    }
}
